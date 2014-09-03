(ns subman.sources.subscene-test
  (:require [clojure.test :refer [deftest testing]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is=]]
            [subman.sources.subscene :as subscene]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]
            [subman.const :as const]))

(def page-content (get-from-file
                    "resources/fixtures/subman/sources/subscene_page.html"))

(def release-content (get-from-file
                       "resources/fixtures/subman/sources/subscene_release.html"))

(deftest test-make-url
  (is= (#'subscene/make-url "/test") "http://subscene.com/test"))

(deftest test-get-page-url
  (is= (#'subscene/get-page-url 1) "http://subscene.com/browse/latest/series/1"))

(deftest test-remove-spec-symbols
  (is= (#'subscene/remove-spec-symbols "\t\ntest\t\n") "test"))

(deftest test-get-version
  (is= (#'subscene/get-version page-content)
       "The.Following.S02E05.720p.HDTV.X264-DIMENSION, The.Following.S02E05.480p.HDTV.x264-Micromkv, The.Following.S02E05.480p.HDTV.x264-mSD, The.Following.S02E05.HDTV.x264-LOL, The.Following.S02E05.HDTV.XviD-FUM, The.Following.S02E05.HDTV.XviD-AFG, The.Following.S02E05.HDTV.x264-GWC"))

(deftest test-page-content
  (is= (#'subscene/get-show page-content) "The Following - Second Season"))

(deftest test-get-lang
  (is= (#'subscene/get-lang page-content) "English"))

(deftest test-create-subtitle
  (with-redefs [helpers/fetch (constantly page-content)]
    (is= (#'subscene/create-subtitle "") {:episode "5"
                                          :lang "English"
                                          :season "2"
                                          :show "The Following - Second Season"
                                          :source const/type-subscene
                                          :url ""
                                          :version "The.Following.S02E05.720p.HDTV.X264-DIMENSION, The.Following.S02E05.480p.HDTV.x264-Micromkv, The.Following.S02E05.480p.HDTV.x264-mSD, The.Following.S02E05.HDTV.x264-LOL, The.Following.S02E05.HDTV.XviD-FUM, The.Following.S02E05.HDTV.XviD-AFG, The.Following.S02E05.HDTV.x264-GWC"})))

(deftest test-get-release-page-result
  (with-redefs [helpers/fetch #(if (= % "http://subscene.com/browse/latest/series/1")
                                release-content
                                page-content)]
    (is= "The Following - Second Season"
         (:show (first (subscene/get-release-page-result 1))))))
