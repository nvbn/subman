(ns subman.web.t-api
  (:require [midje.sweet :refer [fact facts => provided anything
                                 truthy with-state-changes
                                 before after]]
            [clojure.data.json :as json]
            [subman.models :as models]
            [subman.web.api :as api]))

(facts "get writer"
       (fact "should be json if format = json"
             (#'api/get-writer {:format "json"}) => #(= % json/write-str))
       (fact "else should be prn-str"
             (#'api/get-writer {:format "clojure"}) => #(= % prn-str)))

(facts "search api"
       (fact "should pass correct values"
             (api/search {:query "test"
                          :offset 100
                          :lang "ru"}) => truthy
             (provided
              (models/search :query "test" :offset 100 :lang "ru") => true))
       (fact "should set default values"
             (api/search {:query "test"}) => truthy
             (provided
              (models/search :query "test" :offset 0 :lang "english") => true)))

(let [orig (atom 0)]
  (with-state-changes [(before :facts (do (reset! orig @models/total-count)
                                        (reset! models/total-count 10)))
                       (after :facts (reset! models/total-count @orig))]
    (fact "api should return total count"
          (api/total-count {}) => (prn-str 10))))
