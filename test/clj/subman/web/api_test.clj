(ns subman.web.api-test
  (:require [clojure.test :refer [deftest testing is]]
            [test-sugar.core :refer [is=]]
            [subman.models :as models]
            [subman.web.api :as api]
            [subman.const :as const]))

(deftest test-search
  (testing "should pass correct values"
    (with-redefs [models/search (fn [& args]
                                  (= args [:query "test"
                                           :offset 100
                                           :lang "ru"
                                           :source const/type-podnapisi
                                           :limit 5]))]
      (is (api/search {:query "test"
                       :offset 100
                       :lang "ru"
                       :source const/type-podnapisi
                       :limit 5}))))

  (testing "should set default values"
    (with-redefs [models/search (fn [& args]
                                  (= args [:query "test"
                                           :offset 0
                                           :lang "english"
                                           :source const/type-all
                                           :limit const/result-size]))]
      (is (complement nil?) (api/search {:query "test"})))))

(deftest test-bulk-search
  (testing "should pass correct values"
    (let [calls (atom [])]
      (with-redefs [models/search (fn [& args] (swap! calls conj args))]
        (api/bulk-search {:queries ["first" "second"]})
        (is= @calls [[:query "first"
                      :offset 0
                      :lang "english"
                      :source const/type-all
                      :limit const/result-size]
                     [:query "second"
                      :offset 0
                      :lang "english"
                      :source const/type-all
                      :limit const/result-size]])))))

(deftest test-total-count
  (with-redefs [models/get-total-count (fn [] 10)]
    (is= (api/total-count)
         {:total-count 10})))

(deftest test-list-languages
  (with-redefs [models/list-languages (constantly [{:term "english"
                                                    :count 100}
                                                   {:term "russian"
                                                    :count 50}])]
    (is= (api/list-languages)
         [{:term "english"
           :count 100}
          {:term "russian"
           :count 50}])))

(deftest test-list-sources
  (is= (api/list-sources) const/type-names))
