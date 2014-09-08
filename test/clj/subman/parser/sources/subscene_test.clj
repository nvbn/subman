(ns subman.parser.sources.subscene-test
  (:require [clojure.test :refer [deftest testing]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is=]]
            [subman.parser.sources.subscene :as subscene]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]
            [subman.const :as const]))

(def page-file-name "resources/fixtures/subscene_page.html")

(def page-content (get-from-file page-file-name))

(def page-html (slurp page-file-name))

(def release-file-name "resources/fixtures/subscene_release.html")

(def release-content (get-from-file release-file-name))

(def release-html (slurp release-file-name))

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
  (is= (#'subscene/create-subtitle page-content)
       {:episode "5"
        :lang "English"
        :season "2"
        :show "The Following - Second Season"
        :url "http://subscene.com/subtitle/download?mac=ikZXBjGYgmyy4BOymnZ6mFbLF_YeiqKwsB62_ws6DQGSI_xttB_x5LpWFRUVivJ10"
        :version "The.Following.S02E05.720p.HDTV.X264-DIMENSION, The.Following.S02E05.480p.HDTV.x264-Micromkv, The.Following.S02E05.480p.HDTV.x264-mSD, The.Following.S02E05.HDTV.x264-LOL, The.Following.S02E05.HDTV.XviD-FUM, The.Following.S02E05.HDTV.XviD-AFG, The.Following.S02E05.HDTV.x264-GWC"}))

(deftest test-get-htmls-for-parse
  (with-redefs [helpers/fetch (constantly release-content)
                helpers/download (constantly page-html)]
    (is= (subscene/get-htmls-for-parse 1)
         (repeat 15 page-html))))

(deftest test-get-url
  (is= (subscene/get-url page-content)
       "http://subscene.com/subtitle/download?mac=ikZXBjGYgmyy4BOymnZ6mFbLF_YeiqKwsB62_ws6DQGSI_xttB_x5LpWFRUVivJ10"))

(deftest test-get-subtitles
  (is= (subscene/get-subtitles page-html)
       [{:show "The Following - Second Season"
         :season "2"
         :episode "5"
         :version "The.Following.S02E05.720p.HDTV.X264-DIMENSION, The.Following.S02E05.480p.HDTV.x264-Micromkv, The.Following.S02E05.480p.HDTV.x264-mSD, The.Following.S02E05.HDTV.x264-LOL, The.Following.S02E05.HDTV.XviD-FUM, The.Following.S02E05.HDTV.XviD-AFG, The.Following.S02E05.HDTV.x264-GWC"
         :url "http://subscene.com/subtitle/download?mac=ikZXBjGYgmyy4BOymnZ6mFbLF_YeiqKwsB62_ws6DQGSI_xttB_x5LpWFRUVivJ10"
         :lang "English"}]))
