(ns subman.handlers-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest done testing is]]
            [cljs.core.async :refer [<! timeout]]
            [subman.models :as m]
            [subman.deps :as d]
            [subman.handlers :as h]))

(deftest ^:async test-handle-stable-search-query!
         (let [search-url (atom "")
               state (atom {:stable-search-query ""})]
           (h/handle-stable-search-query! state)
           (go (reset! d/http-get (fn [url]
                                    (reset! search-url url)
                                    (go {:body (prn-str [{:test :test}])})))
               (testing "do nothing without search query change"
                        (swap! state assoc
                               :offset 10)
                        (is (= @search-url "")))
               (swap! state assoc
                      :stable-search-query "test-query")
               (<! (timeout 1000))
               (testing "call server when query changed"
                        (is (re-find #"test-query" @search-url)))
               (testing "update search result"
                        (is (= (:results @state) [{:test :test}])))
               (testing "reset offset"
                        (is (= (:offset @state) 0)))
               (done))))

(deftest ^:async test-handle-total-count!
         (let [state (atom {})]
           (go (reset! d/http-get (fn [_]
                                    (go {:body (prn-str 9999)})))
               (h/handle-total-count! state)
               (<! (timeout 1000))
               (is (= (:total-count @state) 9999))
               (done))))
