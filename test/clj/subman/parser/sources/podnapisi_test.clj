(ns subman.parser.sources.podnapisi-test
  (:require [clojure.test :refer [deftest testing]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is=]]
            [subman.parser.sources.podnapisi :as podnapisi]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]
            [subman.const :as const]))

(def line-content
  (get-from-file "resources/fixtures/subman/sources/podnapisi_line.html"))

(def release-content
  (get-from-file "resources/fixtures/subman/sources/podnapisi_release.html"))

(deftest test-make-url
  (is= (#'podnapisi/make-url "/test") "http://www.podnapisi.net/test"))

(deftest test-season-episode-part
  (testing "when can"
    (is= "6" (#'podnapisi/season-episode-part line-content 2)))
  (testing "when can't"
    (is= "" (#'podnapisi/season-episode-part (get-from-line "<td></td>") 2))))

(deftest test-get-show
  (is= "test" (#'podnapisi/get-show (-> (get-from-line "<b> test </b>")
                                        (html/select [:b])
                                        first))))

(deftest test-get-url
  (is= "http://www.podnapisi.net/test"
       (#'podnapisi/get-url (-> (get-from-line "<a href='/test'></a>")
                                (html/select [:a])
                                first))))

(deftest test-get-version
  (is= (#'podnapisi/get-version line-content)
       "Bitten.S01E06.HDTV.x264-2HD\nBitten.S01E06.HDTV.Xv..."))

(deftest test-get-lang
  (is= "English" (#'podnapisi/get-lang line-content)))

(deftest test-create-subtitle-map
  (is= (#'podnapisi/create-subtitle-map line-content)
       {:episode "6"
        :lang "English"
        :name ""
        :season "1"
        :show "Bitten"
        :url "http://www.podnapisi.net/ru/bitten-2014-subtitles-p2920797"
        :version "Bitten.S01E06.HDTV.x264-2HD\nBitten.S01E06.HDTV.Xv..."}))

(deftest test-parse-list-page
  (with-redefs [helpers/fetch (constantly release-content)]
    (is= "Lab Rats" (:show (first (#'podnapisi/parse-list-page ""))))))

(deftest test-get-release-page-url
  (is= (#'podnapisi/get-release-page-url 1)
       "http://www.podnapisi.net/en/ppodnapisi/search/sJ/-1/sS/time/sO/desc/sT/-1/sM/0/sA/0/sK//sOA/0/sOT/0/sOL/0/sOI/0/sOE/0/sOD/0/sOH/0/sY//sOCS/0/sFT/0/sR//sTS//sTE//sAKA/1/sH//sI//tbsl/1/asdp/0/page//page/1"))

(deftest test-get-release-page-result
  (with-redefs [helpers/fetch (constantly release-content)]
    (is= const/type-podnapisi (:source (first (#'podnapisi/get-release-page-result 1))))))
