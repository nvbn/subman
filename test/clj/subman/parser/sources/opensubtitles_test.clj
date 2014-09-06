(ns subman.parser.sources.opensubtitles-test
  (:require [clojure.test :refer [deftest testing]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is= is-do]]
            [subman.parser.sources.opensubtitles :as opensubtitles]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]
            [subman.const :as const]))

(def line-content
  (get-from-file "resources/fixtures/opensubtitles_line.html"))

(def release-content
  (get-from-file "resources/fixtures/opensubtitles_release.html"))

(def titles-td (first (html/select line-content [:td])))

(def main-link (first (html/select line-content [:td :strong :a])))

(deftest test-make-url
  (is= (#'opensubtitles/make-url "/test")
       "http://www.opensubtitles.org/test"))

(deftest test-get-page-url
  (is= (#'opensubtitles/get-page-url 2)
       "http://www.opensubtitles.org/en/search/sublanguageid-all/offset-40"))

(deftest test-get-from-season-part
  (testing "when can get"
    (is= (#'opensubtitles/get-from-season-part #"S(\d+)" "S01")
         "1"))
  (testing "when can't"
    (is= (#'opensubtitles/get-from-season-part #"S(\d+)" "123")
         "")))

(deftest test-remove-brs
  (is= (#'opensubtitles/remove-brs "t<br>es<br />t")
       "t es t"))

(deftest test-get-from-show-part
  (testing "when can get"
    (is= (#'opensubtitles/get-from-show-part #"\"(.+)\"" "\"test\"")
         "test"))
  (testing "when can't"
    (is= (#'opensubtitles/get-from-show-part #"\"(.+)\"" "test")
         ""))
  (testing "when can't with default"
    (is= (#'opensubtitles/get-from-show-part #"\"(.+)\"" "test" "1")
         "1")))

(deftest test-get-seasons-part
  (is= (#'opensubtitles/get-seasons-part titles-td)
       "\n\t\t[S01E17]\n\t\tDads (2013) - 01x17 - Enemies of Bill.EXCELLENCE"))

(deftest test-get-show-part
  (is= (#'opensubtitles/get-show-part main-link)
       "\"Dads\" Enemies of Bill\n \t\t\t(2014)"))

(deftest test-get-url
  (is= (#'opensubtitles/get-url main-link)
       "http://www.opensubtitles.org/en/subtitles/5547771/dads-enemies-of-bill-en"))

(deftest test-get-version
  (is= (#'opensubtitles/get-version titles-td) ""))

(deftest test-create-subtitle
  (is= (#'opensubtitles/create-subtitle (-> line-content
                                            (html/select [:tr])
                                            first))
       {:episode "17"
        :lang "English"
        :name "Enemies of Bill"
        :season "1"
        :show "Dads"
        :source const/type-opensubtitles
        :url "http://www.opensubtitles.org/en/subtitles/5547771/dads-enemies-of-bill-en"
        :version ""}))

(deftest test-get-release-page-result
  (with-redefs [helpers/fetch (constantly release-content)]
    (is= (:show (first (opensubtitles/get-release-page-result 1)))
         "Community")))
