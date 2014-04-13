(ns subman.sources.t-opensubtitles
  (:require [midje.sweet :refer [facts fact anything => provided]]
            [net.cgrand.enlive-html :as html]
            [subman.sources.opensubtitles :as opensubtitles]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]
            [subman.const :as const]))

(def line-content
  (get-from-file "test/fixtures/subman/sources/opensubtitles_line.html"))

(def release-content
  (get-from-file "test/fixtures/subman/sources/opensubtitles_release.html"))

(def titles-td (first (html/select line-content [:td])))

(def main-link (first (html/select line-content [:td :strong :a])))

(fact "should make absolute url"
      (#'opensubtitles/make-url "/test") => "http://www.opensubtitles.org/test")

(fact "should get page url"
      (#'opensubtitles/get-page-url 2)
      => "http://www.opensubtitles.org/en/search/sublanguageid-all/offset-40")

(facts "get from season part"
       (fact "when can get"
             (#'opensubtitles/get-from-season-part #"S(\d+)" "S01") => "1")
       (fact "when can't"
             (#'opensubtitles/get-from-season-part #"S(\d+)" "123") => ""))

(fact "should remove brs"
      (#'opensubtitles/remove-brs "t<br>es<br />t") => "t es t")

(facts "get from show part"
       (fact "when can get"
             (#'opensubtitles/get-from-show-part #"\"(.+)\"" "\"test\"") => "test")
       (fact "when can't"
             (#'opensubtitles/get-from-show-part #"\"(.+)\"" "test") => "")
       (fact "when can't with default"
             (#'opensubtitles/get-from-show-part #"\"(.+)\"" "test" "1") => "1"))

(fact "should get seasons part"
      (#'opensubtitles/get-seasons-part
       titles-td) => "\n\t\t[S01E17]\n\t\tDads (2013) - 01x17 - Enemies of Bill.EXCELLENCE")

(fact "should return show part"
      (#'opensubtitles/get-show-part
       main-link) => "\"Dads\" Enemies of Bill\n \t\t\t(2014)")

(fact "should return url"
      (#'opensubtitles/get-url
       main-link) => "http://www.opensubtitles.org/en/subtitles/5547771/dads-enemies-of-bill-en")

(fact "should return version"
      (#'opensubtitles/get-version titles-td) => "")

(fact "should create subtitle map"
      (#'opensubtitles/create-subtitle
       (-> line-content
           (html/select [:tr])
           first)) => {:episode "17"
                       :lang "English"
                       :name "Enemies of Bill"
                       :season "1"
                       :show "Dads"
                       :source const/type-opensubtitles
                       :url "http://www.opensubtitles.org/en/subtitles/5547771/dads-enemies-of-bill-en"
                       :version ""})

(fact "should get subtitles from release page"
      (-> (opensubtitles/get-release-page-result 1)
          first
          :show) => "Community"
      (provided
       (helpers/fetch anything) => release-content))
