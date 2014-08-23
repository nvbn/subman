(ns subman.web.api-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.data.json :as json]
            [test-sugar.core :refer [is=]]
            [subman.models :as models]
            [subman.web.api :as api]
            [subman.const :as const]))

(deftest test-api-search
  (testing "should pass correct values"
    (with-redefs [models/search (fn [& args]
                                  (= args [:query "test"
                                           :offset 100
                                           :lang "ru"
                                           :source const/type-podnapisi]))]
                 (is (api/search {:query  "test"
                                  :offset 100
                                  :lang   "ru"
                                  :source const/type-podnapisi}))))

  (testing "should set default values"
    (with-redefs [models/search (fn [& args]
                                  (= args [:query "test"
                                           :offset 0
                                           :lang "english"
                                           :source const/type-all]))]
                 (is (complement nil?) (api/search {:query "test"})))))

(deftest test-api-total-count
  (with-redefs [models/get-total-count (fn [] 10)]
               (is= (api/total-count)
                    {:total-count 10})))

(deftest test-api-list-languages
  (with-redefs [models/list-languages (constantly [{:term  "english"
                                                    :count 100}
                                                   {:term  "russian"
                                                    :count 50}])]
               (is= (api/list-languages)
                    [{:term  "english"
                      :count 100}
                     {:term  "russian"
                      :count 50}])))
