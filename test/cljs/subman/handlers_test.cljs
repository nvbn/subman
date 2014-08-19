(ns subman.handlers-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest done testing is]]
            [cljs.core.async :refer [<! timeout]]
            [subman.models :as m]
            [subman.locks :as l]
            [subman.deps :as d]
            [subman.handlers :as h]))

(deftest ^:async test-handle-stable-search-query
         (let [search-url (atom "")
               state (atom {:stable-search-query ""})]
           (h/handle-stable-search-query! state)
           (go (<! (l/take-http!))
               (println "start handler")
               (reset! d/http-get (fn [url]
                                    (println "handler http")
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
               (println "done handler")
               (l/free-http!)
               (done))))
