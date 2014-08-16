(ns subman.components.core-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest done testing is]]
            [cljs.core.async :refer [<! timeout]]
            [om.core :as om :include-macros true]
            [jayq.core :refer [$]]
            [subman.helpers :refer [render-node]]
            [subman.components.core :refer [page]]))

(deftest test-page
  (go (let [state (atom {:search-query ""})
            [_ $el] (<! (render-node page state))]
        (testing "show welcome page withput query"
          (is (re-find #"Welcome" (.html $el)))
          (testing "show result with query"
            (swap! state assoc :search-query "test")
            (<! (timeout 1000))
            (is (re-find #"Nothing" (.html $el))))))))
