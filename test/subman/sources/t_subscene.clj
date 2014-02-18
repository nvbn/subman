(ns subman.sources.t-subscene
  (:require [midje.sweet :refer [facts fact anything => provided]]
            [net.cgrand.enlive-html :as html]
            [subman.sources.subscene :as subscene]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]
            [subman.const :as const]))

(def page-content (get-from-file
                   "test/subman/sources/fixtures/subscene_page.html"))

(def release-content (get-from-file
                   "test/subman/sources/fixtures/subscene_release.html"))

(fact "should make absolute url"
      (#'subscene/make-url "/test") => "http://subscene.com/test")

(fact "should get page url"
      (#'subscene/get-page-url 1) => "http://subscene.com/browse/latest/series/1")

(fact "should remove special symbols"
      (#'subscene/remove-spec-symbols "\t\ntest\t\n") => "test")

(fact "should get version"
      (#'subscene/get-version
       page-content) => "The.Following.S02E05.720p.HDTV.X264-DIMENSION, The.Following.S02E05.480p.HDTV.x264-Micromkv, The.Following.S02E05.480p.HDTV.x264-mSD, The.Following.S02E05.HDTV.x264-LOL, The.Following.S02E05.HDTV.XviD-FUM, The.Following.S02E05.HDTV.XviD-AFG, The.Following.S02E05.HDTV.x264-GWC")

(fact "should get show"
      (#'subscene/get-show page-content) => "The Following - Second Season")

(fact "should get language"
      (#'subscene/get-lang page-content) => "English")

(fact "should create subtitle map"
      (#'subscene/create-subtitle "") => {:episode "5"
                                          :lang "English"
                                          :season "2"
                                          :show "The Following - Second Season"
                                          :source const/type-subscene
                                          :url ""
                                          :version "The.Following.S02E05.720p.HDTV.X264-DIMENSION, The.Following.S02E05.480p.HDTV.x264-Micromkv, The.Following.S02E05.480p.HDTV.x264-mSD, The.Following.S02E05.HDTV.x264-LOL, The.Following.S02E05.HDTV.XviD-FUM, The.Following.S02E05.HDTV.XviD-AFG, The.Following.S02E05.HDTV.x264-GWC"}
      (provided (helpers/fetch anything) => page-content))

(fact "should parse release page"
      (-> (subscene/get-release-page-result 1)
          first
          :show) => "The Following - Second Season"
      (provided
       (helpers/fetch
        "http://subscene.com/browse/latest/series/1") => release-content
       (helpers/fetch anything) => page-content))
