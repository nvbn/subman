(ns subman.sources.notabenoid-test
  (:require [clojure.test :refer [deftest testing]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is= is-do]]
            [subman.const :as const]
            [subman.sources.notabenoid :as notabenoid]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]))

(def release-page
  (get-from-file "resources/fixtures/subman/sources/notabenoid_release.html"))

(def book-page
  (get-from-file "resources/fixtures/subman/sources/notabenoid_book.html"))

(def release-line
  (get-from-line "<a href=\"/book/36828\">Da Vinci's Demons</a>"))

(def episode-line
  (get-from-file "resources/fixtures/subman/sources/notabenoid_episode.html"))

(def episode-line-not-ready
  (get-from-file "resources/fixtures/subman/sources/notabenoid_not_ready.html"))

(deftest test-make-url
  (is= (#'notabenoid/make-url "/test") "http://notabenoid.com/test"))

(deftest test-get-release-page-url
  (is= (#'notabenoid/get-release-page-url 15)
       "http://notabenoid.com/search/index/t//cat/1/s_lang/0/t_lang/1/ready/1/gen/1/sort/4/Book_page/15"))

(deftest test-book-from-line
  (with-redefs [helpers/fetch (constantly "content")]
    (is= (#'notabenoid/book-from-line release-line) "content")))

(deftest test-get-book-title
  (is= (#'notabenoid/get-book-title book-page)
       "Shetland"))

(deftest test-episode-ready
  (testing "when ready"
    (is-do (complement nil?) (#'notabenoid/episode-ready? episode-line)))
  (testing "when not"
    (is-do nil? (#'notabenoid/episode-ready? episode-line-not-ready))))

(deftest test-get-season-episode
  (testing "when exists"
    (is= (#'notabenoid/get-season-episode "Bones - 09x18 - The Carrot in the Kudzu")
         ["9" "18"]))
  (testing "when in begining of string"
    (is= (#'notabenoid/get-season-episode "91x18 - The Carrot in the Kudzu")
         ["91" "18"]))
  (testing "when in end of string"
    (is= (#'notabenoid/get-season-episode "Bones - 09x181")
         ["9" "181"]))
  (testing "when in S01E01 format"
    (is= (#'notabenoid/get-season-episode "Family guy S03E12")
         ["3" "12"]))
  (testing "when not"
    (is= (#'notabenoid/get-season-episode "cat eat")
         ["" ""])))

(deftest test-get-episode-name
  (is= (#'notabenoid/get-episode-name "Bones - 09x18 - The Carrot in the Kudzu")
       "The Carrot in the Kudzu"))

(deftest test-episode-title-line
  (is= (#'notabenoid/get-episode-title-line episode-line)
       "Bones - 09x14 - The Master In The Slop"))

(deftest test-get-episode-url
  (is= (#'notabenoid/get-episode-url episode-line)
       "http://notabenoid.com/book/43718/195318/ready"))

(deftest test-episode-from-line
  (is= (#'notabenoid/episode-from-line episode-line)
       {:episode "14"
        :lang "russian"
        :name "The Master In The Slop"
        :season "9"
        :url "http://notabenoid.com/book/43718/195318/ready"
        :version ""
        :source const/type-notabenoid}))

(deftest test-episodes-from-book
  (testing "should create for all ready episodes"
    (is= (count (#'notabenoid/episodes-from-book book-page)) 7))
  (testing "should set show name"
    (is= (:show (first (#'notabenoid/episodes-from-book book-page)))
         "Shetland")))

(deftest test-get-release-page-result
  (with-redefs [helpers/fetch #(if (= % (#'notabenoid/get-release-page-url 1))
                                release-page
                                book-page)]
    (is= 350 (count (notabenoid/get-release-page-result 1)))))
