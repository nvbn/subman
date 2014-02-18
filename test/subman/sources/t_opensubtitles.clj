(ns subman.sources.t-opensubtitles
  (:require [midje.sweet :refer [facts fact anything => provided]]
            [net.cgrand.enlive-html :as html]
            [subman.sources.opensubtitles :as opensubtitles]
            [subman.helpers :as helpers :refer [get-from-file]]
            [subman.const :as const]))

(fact "should make absolute url"
      (#'opensubtitles/make-url "/test") => "http://www.opensubtitles.org/test")

(fact "should get page url"
      (#'opensubtitles/get-page-url 2)
      => "http://www.opensubtitles.org/en/search/sublanguageid-eng/offset-40")

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

(fact "should create subtitle map"
      (#'opensubtitles/create-subtitle
       (-> (get-from-file "test/subman/sources/fixtures/opensubtitles_line.html")
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
       (helpers/fetch anything) => (get-from-file
                                    "test/subman/sources/fixtures/opensubtitles_release.html")))
