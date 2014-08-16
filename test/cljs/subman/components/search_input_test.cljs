(ns subman.components.search-input-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest done testing is]]
            [cljs.core.async :refer [timeout >!]]
            [om.core :as om :include-macros true]
            [jayq.core :refer [$]]
            [subman.const :as const]
            [subman.helpers :refer [render-node]]
            [subman.components.search-input :refer [search-input]]))

(deftest ^:async test-search-input
  (let [state (atom {:search-query "initial"})
        owner (atom nil)]
    (go (let [[owner $el] (<! (render-node search-input state))
              input (.find $el ".search-input")]
          (testing "set initial value"
            (is (= (.val input) "initial")))
          (testing "not change app state eager"
            (.val input "test")
            (js/React.addons.TestUtils.Simulate.change
             (om/get-node owner))
            (is (not= (:stable-search-query state) "test")))
          (testing "change app state when value is stable"
            (<! (timeout (* const/input-timeout 2)))
            (is (= (:stable-search-query @state) "test")))
          (done)))))
