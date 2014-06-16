(ns subman.sources.t-uksubtitles
  (:require [midje.sweet :refer [facts fact anything => provided]]
            [net.cgrand.enlive-html :as html]
            [subman.const :as const]
            [subman.sources.uksubtitles :as uksubtitles]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]))

(def release-page
  (get-from-file "test/fixtures/subman/sources/uksubtitles_page.html"))

(def article
  (get-from-file "test/fixtures/subman/sources/uksubtitles_article.html"))

(def article-balnk
  (get-from-file "test/fixtures/subman/sources/uksubtitles_article_blank.html"))

(facts "get release page url"
       (fact "for first page"
             (#'uksubtitles/get-release-page-url 1) => "http://uksubtitles.ru/")
       (fact "for not first page"
             (#'uksubtitles/get-release-page-url 3) => "http://uksubtitles.ru/page/3/"))

(fact "get articles from page"
      (count (#'uksubtitles/get-articles release-page)) => 10)

(facts "parse article"
       (fact "with single subtitle entries"
             (#'uksubtitles/parse-article article)
             => {:subtitles ["\ncardinal.burns.s02e03.hdtv.x264-river.srt (31.3 KiB, Download: 33 )\n"
                             "\ncardinal.burns.s02e03.720p.hdtv.x264-tla.srt (31.3 KiB, Download: 15 )\n"]
                 :title "Subtitles for 3 episode Series 2 Cardinal Burns, S02E03 – Episode 3 (Channel 4)."
                 :url "http://uksubtitles.ru/subtitles-for-3-episode-series-2-cardinal-burns-s02e03-episode-3-channel-4/"})
       (fact "without subtitles entries"
             (#'uksubtitles/parse-article article-balnk)
             => {:subtitles []
                 :title "Subtitles for 4-8 episodes The Smoke, S01E04-08 – Episodes 4-8 (Sky1)."
                 :url "http://uksubtitles.ru/subtitles-for-4-8-episodes-the-smoke-s01e04-08-episodes-4-8-sky1/"}))

(facts "get name from donwload line"
       (fact "with season/episode"
             (#'uksubtitles/get-name-from-download "\nThe.Moodys.S02E08.PDTV.x264-BATV.srt (43.1 KiB, Download: 29 )")
             => "The Moodys")
       (fact "with quality postfix"
             (#'uksubtitles/get-name-from-download "game.face.720p.hdtv.x264-tla.srt (29.8 KiB, Download: 61 )")
             => "game face")
       (fact "with extension"
             (#'uksubtitles/get-name-from-download "\nlast.pays.srt")
             => "last pays")
       (fact "without nothing"
             (#'uksubtitles/get-name-from-download "test.subtitles")
             => "test subtitles"))

(facts "get subtitle data from download line"
       (fact "with season and episode"
             (#'uksubtitles/get-subtitle-data-from-download "The.Moodys.S02E08.PDTV.x264-BATV.srt (43.1 KiB, Download: 29 )")
             => {:episode "8"
                 :name "The Moodys"
                 :season "2"
                 :version "The.Moodys.S02E08.PDTV.x264-BATV.srt"})
       (fact "without season and episode"
             (#'uksubtitles/get-subtitle-data-from-download "\ngame.face.720p.hdtv.x264-tla.srt (29.8 KiB, Download: 61 )")
             => {:episode ""
                 :name "game face"
                 :season ""
                 :version
                 "\ngame.face.720p.hdtv.x264-tla.srt"}))

(facts "get subtitles from article"
       (fact "with single subtitle entries"
             (#'uksubtitles/get-subtitles-from-article (#'uksubtitles/parse-article article))
             => [{:episode "3"
                  :lang "english"
                  :name "cardinal burns"
                  :season "2"
                  :source const/type-uksubtitles
                  :url "http://uksubtitles.ru/subtitles-for-3-episode-series-2-cardinal-burns-s02e03-episode-3-channel-4/"
                  :version "\ncardinal.burns.s02e03.hdtv.x264-river.srt\n"}
                 {:episode "3"
                  :lang "english"
                  :name "cardinal burns"
                  :season "2"
                  :source const/type-uksubtitles
                  :url "http://uksubtitles.ru/subtitles-for-3-episode-series-2-cardinal-burns-s02e03-episode-3-channel-4/"
                  :version "\ncardinal.burns.s02e03.720p.hdtv.x264-tla.srt\n"}])
       (fact "without subtitle entries"
             (#'uksubtitles/get-subtitles-from-article (#'uksubtitles/parse-article article-balnk))
             => [{:lang "english"
                  :name "4-8 episodes The Smoke, S01E04-08 – Episodes 4-8 (Sky1).",
                  :source const/type-uksubtitles
                  :url "http://uksubtitles.ru/subtitles-for-4-8-episodes-the-smoke-s01e04-08-episodes-4-8-sky1/"}]))

(fact "get release page result"
      (count (uksubtitles/get-release-page-result 0)) => 12
      (provided (helpers/fetch anything) => release-page))
