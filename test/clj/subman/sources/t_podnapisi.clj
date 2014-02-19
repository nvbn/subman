(ns subman.sources.t-podnapisi
  (:require [midje.sweet :refer [facts fact anything => provided]]
            [net.cgrand.enlive-html :as html]
            [subman.sources.podnapisi :as podnapisi]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]
            [subman.const :as const]))

(def line-content
  (get-from-file "test/fixtures/subman/sources/podnapisi_line.html"))

(def release-content
  (get-from-file
   "test/fixtures/subman/sources/podnapisi_release.html"))

(fact "should make absolute url"
      (#'podnapisi/make-url "/test") => "http://www.podnapisi.net/test")

(facts "get item from season-episode part"
       (fact "when can"
             (#'podnapisi/season-episode-part line-content 2) => "6")
       (fact "when can't"
             (#'podnapisi/season-episode-part
              (get-from-line "<td></td>") 2) => ""))

(fact "should get show"
      (#'podnapisi/get-show (-> (get-from-line "<b> test </b>")
                                (html/select [:b])
                                first)) => "test")

(fact "should get url"
      (#'podnapisi/get-url (-> (get-from-line "<a href='/test'></a>")
                               (html/select [:a])
                               first)) => "http://www.podnapisi.net/test")

(fact "should get version"
      (#'podnapisi/get-version line-content) => "Bitten.S01E06.HDTV.x264-2HD\nBitten.S01E06.HDTV.Xv...")

(fact "should get language"
      (#'podnapisi/get-lang line-content) => "English")

(fact "should create subtitle map from line"
      (#'podnapisi/create-subtitle-map
       line-content) => {:episode "6"
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
      (provided (helpers/fetch anything) => release-content))

(fact "should get release page url"
      (#'podnapisi/get-release-page-url 1) => "http://www.podnapisi.net/en/ppodnapisi/search/sJ/-1/sS/time/sO/desc/sT/-1/sM/0/sA/0/sK//sOA/0/sOT/0/sOL/0/sOI/0/sOE/0/sOD/0/sOH/0/sY//sOCS/0/sFT/0/sR//sTS//sTE//sAKA/1/sH//sI//tbsl/1/asdp/0/page//page/1")

(fact "should set source when getting release page results"
      (-> (#'podnapisi/get-release-page-result 1)
          first
          :source) => const/type-podnapisi
      (provided (helpers/fetch anything) => release-content))
