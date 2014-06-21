(ns subman.t-filler
  (:require [midje.sweet :refer [fact facts => provided anything truthy]]
            [subman.models :as models]
            [subman.sources.addicted :as addicted]
            [subman.sources.podnapisi :as podnapisi]
            [subman.sources.opensubtitles :as opensubtitles]
            [subman.sources.subscene :as subscene]
            [subman.sources.notabenoid :as notabenoid]
            [subman.sources.uksubtitles :as uksubtitles]
            [subman.const :as const]
            [subman.filler :as filler]))

(defn new-getter
  "Fake getter for tests"
  [page]
  (case page
    4 [:exists]
    3 [:exists]
    2 [:fresh :exists]
    1 [:fresh :fresh]
    :default [(do
                (println page)
                page)]))

(facts "should get new results for page"
       (fact "for page with new"
             (#'filler/get-new-for-page new-getter
                                        #{:exists}
                                        1) => [:fresh :fresh])
       (fact "for page without new"
             (#'filler/get-new-for-page new-getter
                                        #{:exists}
                                        3) => []))

(facts "should get new before in lazy sequence"
       (fact "for all"
             (#'filler/get-new-before-seq new-getter
                                          #{:exists}) => [:fresh :fresh :fresh])
       (fact "for page without results"
             (#'filler/get-new-before-seq new-getter
                                          #{:exists}
                                          3) => [])
       (fact "for page greater than update deep"
             (#'filler/get-new-before-seq new-getter
                                          #{:exists}
                                          (inc const/update-deep)) => []))

(fact "should get new before"
      (count (#'filler/get-new-before new-getter
                                      #(= % :exists))) => 3)

(fact "should get all new from nth getters"
      (count (#'filler/get-all-new #(= % :exists)
                                   new-getter
                                   new-getter
                                   new-getter)) => 9)

(fact "update from all sources"
      (filler/update-all) => truthy
      (provided
       (subscene/get-release-page-result 1) => []
       (opensubtitles/get-release-page-result 1) => []
       (addicted/get-release-page-result 1) => []
       (podnapisi/get-release-page-result 1) => []
       (notabenoid/get-release-page-result 1) => []
       (uksubtitles/get-release-page-result 1) => []))
