(ns subman.search-test
  (:require [cemerick.cljs.test :refer-macros [deftest testing is]]
            [test-sugar.core :refer [is=]]
            [reagent.core :refer [atom]]
            [subman.search :as search]))

(def props {:current-source (atom "all")
            :current-language (atom "english")})

(deftest test-create-search-request
  (testing "with query"
    (is= (search/create-search-request "test" 0 props)
         "/api/search/?lang=english&source=-1&query=test&offset=0"))
  (testing "with query and lang"
    (is= (search/create-search-request "test :lang ru" 0 props)
         "/api/search/?lang=ru&source=-1&query=test&offset=0"))
  (testing "with offset"
    (is= (search/create-search-request "test"
                                       100 props)
         "/api/search/?lang=english&source=-1&query=test&offset=100"))
  (testing "with source"
    (is= (search/create-search-request "test :source addicted"
                                       0 props)
         "/api/search/?lang=english&source=0&query=test&offset=0"))
  (testing "with source and lang"
    (is= (search/create-search-request "test :source addicted :lang uk"
                                       0 props)
         "/api/search/?lang=uk&source=0&query=test&offset=0")))

(deftest test-watch-query
  (is (search/watch-to-query (atom "")
                             (atom [])
                             (atom 0)
                             (atom 0)
                             (atom false)
                             (atom {}))))

(deftest test-get-source-id
  (testing "for source"
    (is= 0 (search/get-source-id "addicted")))
  (testing "for source in wrong case"
    (is= 1 (search/get-source-id "podNApisi")))
  (testing "with source = all"
    (is= -1 (search/get-source-id "all")))
  (testing "with wrong source"
    (is= -2 (search/get-source-id "wtf-this-source"))))
