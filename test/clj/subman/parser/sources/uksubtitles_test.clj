(ns subman.parser.sources.uksubtitles-test
  (:require [clojure.test :refer [deftest testing]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is=]]
            [subman.const :as const]
            [subman.parser.sources.uksubtitles :as uksubtitles]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]))

(def release-page
  (get-from-file "resources/fixtures/subman/sources/uksubtitles_page.html"))

(def article
  (get-from-file "resources/fixtures/subman/sources/uksubtitles_article.html"))

(def article-balnk
  (get-from-file "resources/fixtures/subman/sources/uksubtitles_article_blank.html"))

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
          :version
          "\ngame.face.720p.hdtv.x264-tla.srt"})))

(deftest test-get-subtitles-from-article
  (testing "with single subtitle entries"
    (is= (#'uksubtitles/get-subtitles-from-article (#'uksubtitles/parse-article article))
         [{:episode "3"
           :lang "english"
           :show "cardinal burns"
           :name ""
           :season "2"
           :source const/type-uksubtitles
           :url "http://uksubtitles.ru/subtitles-for-3-episode-series-2-cardinal-burns-s02e03-episode-3-channel-4/"
           :version "\ncardinal.burns.s02e03.hdtv.x264-river.srt\n"}
          {:episode "3"
           :lang "english"
           :show "cardinal burns"
           :name ""
           :season "2"
           :source const/type-uksubtitles
           :url "http://uksubtitles.ru/subtitles-for-3-episode-series-2-cardinal-burns-s02e03-episode-3-channel-4/"
           :version "\ncardinal.burns.s02e03.720p.hdtv.x264-tla.srt\n"}]))
  (testing "without subtitle entries"
    (is= (#'uksubtitles/get-subtitles-from-article (#'uksubtitles/parse-article article-balnk))
         [{:lang "english"
           :show "4-8 episodes The Smoke, S01E04-08 – Episodes 4-8 (Sky1).",
           :source const/type-uksubtitles
           :url "http://uksubtitles.ru/subtitles-for-4-8-episodes-the-smoke-s01e04-08-episodes-4-8-sky1/"
           :name ""}])))

(deftest test-get-release-page-result
  (with-redefs [helpers/fetch (constantly release-page)]
    (is= 12 (count (uksubtitles/get-release-page-result 0)))))
