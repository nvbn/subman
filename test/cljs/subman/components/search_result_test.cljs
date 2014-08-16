(ns subman.components.search-result-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest done testing is]]
            [cljs.core.async :refer [timeout <!]]
            [om.core :as om :include-macros true]
            [jayq.core :refer [$]]
            [subman.const :as const]
            [subman.helpers :refer [render-node]]
            [subman.components.search-result :refer [search-result]]))

(deftest ^:async test-search-result
  (go (let [[_ $el] (<! (render-node search-result
                                     {:results [{:show "Simpsons"}
                                                {:show "American Dad"}]}))]
        (testing "count of search results"
          (is (= 2 (count (.find $el ".result-entry")))))
        (testing "content of search results"
          (let [html (.html $el)]
            (is (re-find #"Simpsons" html))
            (is (re-find #"American Dad" html))))
        (done))))

(deftest ^:async test-search-result-when-in-progress
  (go (let [[_ $el] (<! (render-node search-result
                                     {:results []
                                      :in-progress true}))]
        (let [html (.html $el)]
          (is (re-find #"Searching" html)))
        (done))))

(deftest ^:async test-search-result-without-result
  (go (let [[_ $el] (<! (render-node search-result
                                     {:results []
                                      :search-query "Test query"}))]
        (let [html (.html $el)]
          (is (re-find #"Nothing found" html))
          (is (re-find #"Test query" html)))
        (done))))
