(ns subman.t-models
  (:require [midje.sweet :refer [fact facts => provided anything
                                 with-state-changes before after]]
            [clojurewerkz.elastisch.rest.document :as esd]
            [subman.const :as const]
            [subman.models :as models]))

(let [orig (atom 0)]
  (with-state-changes [(before :facts (reset! orig @models/total-count))
                       (after :facts (reset! models/total-count @orig))]
    (fact "should update total count"
          (models/update-total-count) => 10
          (provided
           (esd/search @models/connection
                       anything anything
                       anything anything) => {:hits {:total 10}}))))

(facts "get season-episode parts"
       (fact "with SnEn notation"
             (#'models/get-season-episode-parts
              "test s01e10") => ["s01e10" "01" "10"])
       (fact "with nxn notation"
             (#'models/get-season-episode-parts
              "test 01x10") => ["01x10" "01" "10"])
       (fact "or nil"
             (#'models/get-season-episode-parts
              "test") => nil))

(facts "get season-episode query part"
       (fact "with season and episode"
             (#'models/get-season-episode
              "test s01e01") => [{:term {:season "1"}}
                                 {:term {:episode "1"}}])
       (fact "without"
             (#'models/get-season-episode "test") => []))

(facts "search query"
       (fact "should build query"
             (#'models/build-query
              "Dads.2013.S01E18.HDTV.x264-EXCELLENCE[rartv]"
              "en"
              const/type-all) => [:query {:bool {:must
                                                 [{:fuzzy_like_this
                                                   {:boost 5
                                                    :fields [:show :name]
                                                    :like_text "Dads 2013 S01E18 HDTV x264-EXCELLENCE[rartv]"}}
                                                  {:term {:season "1"}}
                                                  {:term {:episode "18"}}]
                                                 :should {:fuzzy_like_this
                                                          {:boost 2
                                                           :fields [:version]
                                                           :like_text "Dads 2013 S01E18 HDTV x264-EXCELLENCE[rartv]"}}}}
                                  :filter {:term {:lang "en"}} :size 100])
       (fact "should build query with filter by source"
             (#'models/build-query
              "query"
              "ru"
              const/type-addicted) => [:query {:bool {:must
                                                      [{:fuzzy_like_this
                                                        {:boost 5
                                                         :fields [:show :name]
                                                         :like_text "query"}}
                                                       {:term {:source 0}}]
                                                      :should {:fuzzy_like_this
                                                               {:boost 2
                                                                :fields [:version]
                                                                :like_text "query"}}}}
                                       :filter {:term {:lang "ru"}} :size 100]))

(fact "should return search result"
      (models/search :query "test"
                     :offset 10
                     :lang "en") => ["test"]
      (provided
       (esd/search @models/connection
                   anything anything
                   :from 10
                   :query anything
                   :filter anything
                   :size const/result-size) => {:hits {:hits [{:_source "test"}]}}))

(fact "should check is subtitle exists in db"
      (models/in-db {:url "test"}) => true
      (provided
       (esd/search @models/connection anything anything
                   :filter {:term {:url "test"}}) => {:hits {:total 5}}))

(fact "should list available languages with count"
      (models/list-languages) => [{:term "english"
                                   :count 100}
                                  {:term "russian"
                                   :count 50}]
      (provided
       (esd/search @models/connection anything anything
                   :query anything
                   :facets anything) => {:facets {:tag {:terms [{:term "english"
                                                                 :count 100}
                                                                {:term "russian"
                                                                 :count 50}]}}}))
