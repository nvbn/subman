(ns subman.web.api-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.data.json :as json]
            [test-sugar.core :refer [is= with-provided]]
            [subman.models :as models]
            [subman.web.api :as api]
            [subman.const :as const]))

(deftest test-get-writer
  (testing "should be json if format = json"
    (is= (#'api/get-writer {:format "json"}) json/write-str))
  (testing "else should be prn-str"
    (is= (#'api/get-writer {:format "clojure"}) prn-str)))

(deftest test-api-search
  (testing "should pass correct values"
    (with-provided {#'models/search (fn [& args]
                                      (= args [:query "test"
                                               :offset 100
                                               :lang "ru"
                                               :source const/type-podnapisi]))}
      (is (api/search {:query "test"
                       :offset 100
                       :lang "ru"
                       :source const/type-podnapisi}))))

  (testing "should set default values"
    (with-provided {#'models/search (fn [& args]
                                      (= args [:query "test"
                                               :offset 0
                                               :lang "english"
                                               :source const/type-all]))}
      (is (complement nil?) (api/search {:query "test"})))))

(deftest test-api-total-count
  (let [orig (atom @models/total-count)]
    (reset! models/total-count 10)
    (try (is= (api/total-count {}) (prn-str 10))
      (finally (reset! models/total-count @orig)))))

(deftest test-api-list-languages
  (with-provided {#'models/list-languages (constantly [{:term "english"
                                                        :count 100}
                                                       {:term "russian"
                                                        :count 50}])}
    (is= (api/list-languages {}) (prn-str [{:term "english"
                                            :count 100}
                                           {:term "russian"
                                            :count 50}]))))
