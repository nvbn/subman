(ns subman.t-search
  (:require-macros [purnam.test :refer [facts fact]])
  (:require [reagent.core :refer [atom]]
            [subman.helpers :refer [truthy]]
            [subman.search :as search]))

(def props {:current-source (atom "all")
            :current-language (atom "english")})

(facts "should create search request"
       (fact "with query"
             (search/create-search-request
              "test" 0 props) => "/api/search/?lang=english&source=-1&query=test&offset=0")
       (fact "with query and lang"
             (search/create-search-request
              "test :lang ru"
              0 props) => "/api/search/?lang=ru&source=-1&query=test&offset=0")
       (fact "with offset"
             (search/create-search-request
              "test" 100 props) => "/api/search/?lang=english&source=-1&query=test&offset=100")
       (fact "with source"
             (search/create-search-request
              "test :source addicted"
              0 props) => "/api/search/?lang=english&source=0&query=test&offset=0")
       (fact "with source and lang"
             (search/create-search-request
              "test :source addicted :lang uk"
              0 props) => "/api/search/?lang=uk&source=0&query=test&offset=0"))

(fact "watch to query should be ok"
      (search/watch-to-query (atom "")
                             (atom [])
                             (atom 0)
                             (atom 0)
                             (atom false)) => truthy)

(facts "get source id"
       (fact "for source"
             (search/get-source-id "addicted") => 0)
       (fact "for source in wrong case"
             (search/get-source-id "podNApisi") => 1)
       (fact "with source = all"
             (search/get-source-id "all") => -1)
       (fact "with wrong source"
             (search/get-source-id "wtf-this-source") => -2))
