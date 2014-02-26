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
           (esd/search anything anything
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
              false) => [:query {:bool {:must {:fuzzy_like_this
                                               {:like_text "Dads 2013 S01E18 HDTV x264-EXCELLENCE[rartv]"}}
                                        :should [{:term {:season "1"}}
                                                 {:term {:episode "18"}}]}}
                         :filter {:term {:lang "en"}}
                         :size const/result-size])
       (fact "should build exact query"
             (#'models/build-query
              "Dads.2013.S01E18.HDTV.x264-EXCELLENCE[rartv]"
              "en"
              true) => [:query {:bool {:must {:fuzzy_like_this
                                               {:like_text "Dads 2013 S01E18 HDTV x264-EXCELLENCE[rartv]"}}
                                        :should [{:term {:season "1"}}
                                                 {:term {:episode "18"}}]}}
                         :filter {:and [{:term {:season "1"}}
                                        {:term {:episode "18"}}
                                        {:term {:lang "en"}}]}
                         :size const/result-size]))

(fact "should return search result"
      (models/search :query "test"
                     :offset 10
                     :lang "en"
                     :exact false) => ["test"]
      (provided
       (esd/search anything anything
                   :from 10
                   :query anything
                   :filter anything
                   :size const/result-size) => {:hits {:hits [{:_source "test"}]}}))

(fact "should check is subtitle exists in db"
      (models/in-db {:url "test"}) => true
      (provided
       (esd/search anything anything
                   :filter {:term {:url "test"}}) => {:hits {:total 5}}))
