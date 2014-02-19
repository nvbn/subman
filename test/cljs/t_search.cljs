(ns subman.t-search
  (:require-macros [purnam.test.sweet :refer [facts fact]])
  (:require [reagent.core :refer [atom]]
            [subman.helpers :refer [truthy]]
            [subman.search :as search]))

(facts "should create search request"
       (fact "with query"
             (search/create-search-request "test") => "/api/search/?query=test")
       (fact "with query and lang"
             (search/create-search-request
              "test :lang ru") => "/api/search/?query=test&lang=ru"))

(fact "watch to query should be ok"
      (search/watch-to-query (atom "")
                             (atom [])
                             (atom 0)) => truthy)
