(ns subman.t-search
  (:require-macros [purnam.test :refer [facts fact]])
  (:require [reagent.core :refer [atom]]
            [subman.helpers :refer [truthy]]
            [subman.search :as search]))

(facts "should create search request"
       (fact "with query"
             (search/create-search-request "test" 0) => "/api/search/?query=test&offset=0")
       (fact "with query and lang"
             (search/create-search-request
              "test :lang ru" 0) => "/api/search/?lang=ru&query=ru&offset=0")
       (fact "with offset"
             (search/create-search-request
              "test" 100) => "/api/search/?query=test&offset=100"))

(fact "watch to query should be ok"
      (search/watch-to-query (atom "")
                             (atom [])
                             (atom 0)
                             (atom 0)
                             (atom false)) => truthy)
