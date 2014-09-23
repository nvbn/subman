(ns subman.parser.sources.uksubtitles-test
  (:require [clojure.test :refer [deftest testing is]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is=]]
            [subman.const :as const]
            [subman.parser.sources.uksubtitles :as uksubtitles]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]))

(def release-page-file-name "resources/fixtures/uksubtitles_page.html")

(def release-page (get-from-file release-page-file-name))

(def release-page-html (slurp release-page-file-name))

(def article
  (get-from-file "resources/fixtures/uksubtitles_article.html"))

(def article-balnk
  (get-from-file "resources/fixtures/uksubtitles_article_blank.html"))

(deftest test-get-release-page-url
  (testing "for first page"
    (is= (#'uksubtitles/get-release-page-url 1) "http://uksubtitles.ru/"))
  (testing "for not first page"
    (is= (#'uksubtitles/get-release-page-url 3) "http://uksubtitles.ru/page/3/")))

(deftest test-get-articles
  (is= 10 (count (#'uksubtitles/get-articles release-page))))

(deftest test-parse-article
  (testing "with single subtitle entries"
    (is= (#'uksubtitles/parse-article article)
         {:subtitles ["\ncardinal.burns.s02e03.hdtv.x264-river.srt (31.3 KiB, Download: 33 )\n"
                      "\ncardinal.burns.s02e03.720p.hdtv.x264-tla.srt (31.3 KiB, Download: 15 )\n"]
          :title "Subtitles for 3 episode Series 2 Cardinal Burns, S02E03 – Episode 3 (Channel 4)."
          :url "http://uksubtitles.ru/subtitles-for-3-episode-series-2-cardinal-burns-s02e03-episode-3-channel-4/"}))
  (testing "without subtitles entries"
    (is= (#'uksubtitles/parse-article article-balnk)
         {:subtitles []
          :title "Subtitles for 4-8 episodes The Smoke, S01E04-08 – Episodes 4-8 (Sky1)."
          :url "http://uksubtitles.ru/subtitles-for-4-8-episodes-the-smoke-s01e04-08-episodes-4-8-sky1/"})))

(deftest test-get-name-from-download
  (testing "with season/episode"
    (is= "The Moodys"
         (#'uksubtitles/get-name-from-download "\nThe.Moodys.S02E08.PDTV.x264-BATV.srt (43.1 KiB, Download: 29 )")))
  (testing "with quality postfix"
    (is= (#'uksubtitles/get-name-from-download "game.face.720p.hdtv.x264-tla.srt (29.8 KiB, Download: 61 )")
         "game face"))
  (testing "with extension"
    (is= (#'uksubtitles/get-name-from-download "\nlast.pays.srt")
         "last pays"))
  (testing "without nothing"
    (is= (#'uksubtitles/get-name-from-download "test.subtitles")
         "test subtitles")))

(deftest test-get-subtitle-data-from-download
  (testing "with season and episode"
    (is= (#'uksubtitles/get-subtitle-data-from-download "The.Moodys.S02E08.PDTV.x264-BATV.srt (43.1 KiB, Download: 29 )")
         {:episode "8"
          :show "The Moodys"
          :season "2"
          :version "The.Moodys.S02E08.PDTV.x264-BATV.srt"}))
  (testing "without season and episode"
    (is= (#'uksubtitles/get-subtitle-data-from-download "\ngame.face.720p.hdtv.x264-tla.srt (29.8 KiB, Download: 61 )")
         {:episode ""
          :show "game face"
          :season ""
          :version "game.face.720p.hdtv.x264-tla.srt"})))

(deftest test-get-subtitles-from-article
  (testing "with single subtitle entries"
    (is= (#'uksubtitles/get-subtitles-from-article (#'uksubtitles/parse-article article))
         [{:episode "3"
           :lang "english"
           :show "cardinal burns"
           :name ""
           :season "2"
           :url "http://uksubtitles.ru/subtitles-for-3-episode-series-2-cardinal-burns-s02e03-episode-3-channel-4/"
           :version "cardinal.burns.s02e03.hdtv.x264-river.srt"}
          {:episode "3"
           :lang "english"
           :show "cardinal burns"
           :name ""
           :season "2"
           :url "http://uksubtitles.ru/subtitles-for-3-episode-series-2-cardinal-burns-s02e03-episode-3-channel-4/"
           :version "cardinal.burns.s02e03.720p.hdtv.x264-tla.srt"}]))
  (testing "without subtitle entries"
    (is= (#'uksubtitles/get-subtitles-from-article (#'uksubtitles/parse-article article-balnk))
         [{:lang "english"
           :show "4-8 episodes The Smoke, S01E04-08 – Episodes 4-8 (Sky1).",
           :url "http://uksubtitles.ru/subtitles-for-4-8-episodes-the-smoke-s01e04-08-episodes-4-8-sky1/"
           :name ""}])))

(deftest test-get-htmls-for-parse
  (with-redefs [helpers/download (constantly release-page-html)]
    (is= (uksubtitles/get-htmls-for-parse 1)
         [{:url "http://uksubtitles.ru/"
           :content release-page-html}])))

(deftest test-get-subtitles
  (is= (uksubtitles/get-subtitles release-page-html "")
       [{:episode "6"
         :lang "english"
         :name ""
         :season "2"
         :show "Cardinal Burns"
         :url "http://uksubtitles.ru/subtitles-for-6-episode-series-2-cardinal-burns-s02e06-episode-6-channel-4/"
         :version "Cardinal.Burns.S02E06.srt"}
        {:episode "8"
         :lang "english"
         :name ""
         :season "8"
         :show "law and order uk"
         :url "http://uksubtitles.ru/subtitles-for-8-episode-of-series-8-law-order-uk-s08e08-repeat-to-fade-itv/"
         :version "law.and.order.uk.s08e08.real.hdtv.x264-river.srt"}
        {:episode "8"
         :lang "english"
         :name ""
         :season "8"
         :show "law_and_order_uk 8x08 repeat_to_fade"
         :url "http://uksubtitles.ru/subtitles-for-8-episode-of-series-8-law-order-uk-s08e08-repeat-to-fade-itv/"
         :version "law_and_order_uk.8x08.repeat_to_fade.720p_hdtv_x264-fov.srt"}
        {:episode "4"
         :lang "english"
         :name ""
         :season "5"
         :show "Offspring"
         :url "http://uksubtitles.ru/subtitles-for-4-episode-series-5-offspring-s05e04-episode-4-ten-au/"
         :version "Offspring.S05E04.PDTV.x264-BATV.srt"}
        {:episode "6"
         :lang "english"
         :name ""
         :season "1"
         :show "happy valley"
         :url "http://uksubtitles.ru/subtitles-for-6-episode-happy-valley-s01e06-episode-6-bbc-one/"
         :version "happy.valley.s01e06.hdtv.x264-tla.srt"}
        {:episode "4"
         :lang "english"
         :name ""
         :season "2"
         :show "a place to call home"
         :url "http://uksubtitles.ru/subtitles-for-4-episode-series-2-a-place-to-call-home-s02e04-what-your-heart-says-seven-au/"
         :version "a.place.to.call.home.s02e04.pdtv.x264-fqm.srt"}
        {:episode "5"
         :lang "english"
         :name ""
         :season "2"
         :show "Cardinal Burns"
         :url "http://uksubtitles.ru/subtitles-for-5-episode-series-2-cardinal-burns-s02e05-episode-5-channel-4/"
         :version "Cardinal.Burns.S02E05.HDTV.x264-TLA.srt"}
        {:episode "6"
         :lang "english"
         :name ""
         :season "2"
         :show "derek"
         :url "http://uksubtitles.ru/subtitles-for-6-episode-series-2-derek-s02e06-episode-6-channel-4/"
         :version "derek.s02e06.hdtv.x264-tla.srt"}
        {:episode "3"
         :lang "english"
         :name ""
         :season "5"
         :show "offspring"
         :url "http://uksubtitles.ru/subtitles-for-3-episode-series-5-offspring-s05e03-episode-3-ten-au/"
         :version "offspring.s05e03.pdtv.x264-fqm.srt"}
        {:episode "5"
         :lang "english"
         :name ""
         :season "1"
         :show "happy valley"
         :url "http://uksubtitles.ru/subtitles-for-5-episode-happy-valley-s01e05-episode-5-bbc-one/"
         :version "happy.valley.s01e05.hdtv.x264-tla.srt"}
        {:episode "5"
         :lang "english"
         :name ""
         :season "1"
         :show "Happy Valley"
         :url "http://uksubtitles.ru/subtitles-for-5-episode-happy-valley-s01e05-episode-5-bbc-one/"
         :version "Happy.Valley.S01E05.720p.HDTV.x264-FTP.srt"}
        {:episode "3"
         :lang "english"
         :name ""
         :season "2"
         :show "a place to call home"
         :url "http://uksubtitles.ru/subtitles-for-3-episode-series-2-a-place-to-call-home-s02e03-a-kiss-to-build-a-dream-on-seven-au/"
         :version "a.place.to.call.home.s02e03.pdtv.x264-fqm.srt"}]))
