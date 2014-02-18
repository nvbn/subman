(ns subman.sources.t-podnapisi
  (:require [midje.sweet :refer [facts fact anything => provided]]
            [net.cgrand.enlive-html :as html]
            [subman.sources.podnapisi :as podnapisi]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]
            [subman.const :as const]))

(fact "should make absolute url"
      (#'podnapisi/make-url "/test") => "http://www.podnapisi.net/test")

(facts "get item from season-episode part"
       (fact "when can"
             (#'podnapisi/season-episode-part
              (get-from-file "test/subman/sources/fixtures/podnapisi_line.html")
              2) => "6")
       (fact "when can't"
             (#'podnapisi/season-episode-part
              (get-from-line "<td></td>") 2) => ""))

(fact "should create subtitle map from line"
      (#'podnapisi/create-subtitle-map
       (get-from-file
        "test/subman/sources/fixtures/podnapisi_line.html")) => {:episode "6"
                                                                 :lang "English"
                                                                 :name ""
                                                                 :season "1"
                                                                 :show "Bitten"
                                                                 :url "http://www.podnapisi.net/ru/bitten-2014-subtitles-p2920797"
                                                                 :version "Bitten.S01E06.HDTV.x264-2HD\nBitten.S01E06.HDTV.Xv..."})

(fact "should get subtitle maps from release page"
      (-> (#'podnapisi/parse-list-page "")
          first
          :show) => "Lab Rats"
      (provided (helpers/fetch anything) => (get-from-file
                                             "test/subman/sources/fixtures/podnapisi_release.html")))
