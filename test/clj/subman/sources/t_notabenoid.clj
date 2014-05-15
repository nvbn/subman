(ns subman.sources.t-notabenoid
  (:require [midje.sweet :refer [facts fact anything => provided]]
            [net.cgrand.enlive-html :as html]
            [subman.const :as const]
            [subman.sources.notabenoid :as notabenoid]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]))

(def release-page
  (get-from-file "test/fixtures/subman/sources/notabenoid_release.html"))

(def book-page
  (get-from-file "test/fixtures/subman/sources/notabenoid_book.html"))

(def release-line
  (get-from-line "<a href=\"/book/36828\">Da Vinci's Demons</a>"))

(def episode-line
  (get-from-file "test/fixtures/subman/sources/notabenoid_episode.html"))

(def episode-line-not-ready
  (get-from-file "test/fixtures/subman/sources/notabenoid_not_ready.html"))

(fact "should make url"
      (#'notabenoid/make-url "/test") => "http://notabenoid.com/test")

(fact "should make release page url"
      (#'notabenoid/get-release-page-url 15) => "http://notabenoid.com/search/index/t//cat/1/s_lang/0/t_lang/1/ready/1/gen/1/sort/4/Book_page/15")

(fact "should get book from line"
      (#'notabenoid/book-from-line release-line) => "content"
      (provided (helpers/fetch anything) => "content"))

(fact "should get book title"
      (#'notabenoid/get-book-title book-page) => "Shetland")

(facts "check episode is ready"
       (fact "when ready"
             (nil? (#'notabenoid/episode-ready? episode-line)) => false)
       (fact "when not"
             (#'notabenoid/episode-ready? episode-line-not-ready) => nil))

(facts "get season episode"
       (fact "when exists"
             (#'notabenoid/get-season-episode
              "Bones - 09x18 - The Carrot in the Kudzu") => ["9" "18"])
       (fact "when in begining of string"
             (#'notabenoid/get-season-episode
              "91x18 - The Carrot in the Kudzu") => ["91" "18"])
       (fact "when in end of string"
             (#'notabenoid/get-season-episode
              "Bones - 09x181") => ["9" "181"])
       (fact "when in S01E01 format"
             (#'notabenoid/get-season-episode
              "Family guy S03E12") => ["3" "12"])
       (fact "when not"
             (#'notabenoid/get-season-episode "cat eat") => ["" ""]))

(fact "get episode name"
      (#'notabenoid/get-episode-name
       "Bones - 09x18 - The Carrot in the Kudzu") => "The Carrot in the Kudzu")

(fact "get episode title line"
      (#'notabenoid/get-episode-title-line
       episode-line) => "Bones - 09x14 - The Master In The Slop")

(fact "get episode url"
      (#'notabenoid/get-episode-url
       episode-line) => "http://notabenoid.com/book/43718/195318/ready")

(fact "get episode map from line"
      (#'notabenoid/episode-from-line
       episode-line) => {:episode "14"
                         :lang "russian"
                         :name "The Master In The Slop"
                         :season "9"
                         :url "http://notabenoid.com/book/43718/195318/ready"
                         :version ""
                         :source const/type-notabenoid})

(facts "get episodes from book"
       (fact "should create for all ready episodes"
             (count (#'notabenoid/episodes-from-book book-page)) => 7)
       (fact "should set show name"
             (-> (#'notabenoid/episodes-from-book book-page)
                 first
                 :show) => "Shetland"))

(fact "get release page result should work"
      (count (notabenoid/get-release-page-result 1)) => 350
      (provided
       (helpers/fetch (#'notabenoid/get-release-page-url 1)) => release-page
       (helpers/fetch anything) => book-page))
