(ns subman.components.welcome-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest done testing is]]
            [cljs.core.async :refer [<!]]
            [om.core :as om :include-macros true]
            [jayq.core :refer [$]]
            [subman.helpers :refer [render-node]]
            [subman.components.welcome :refer [welcome]]))

(deftest ^:async test-welcome
         (go (let [state (atom {:options     {:language {:value     "english"
                                                         :is-sorted true
                                                         :options   ["spain" "russian" "english"]}
                                              :source   {:value   "all"
                                                         :options ["all" "addicted"]}}
                                :total-count 9999})
                   [_ $el] (<! (render-node welcome state))]
               (let [html (.html $el)]
                 (testing "contains sources list"
                          (is (re-find #"addic7ed" html)))
                 (testing "contains language option"
                          (is (re-find #"russian" html)))
                 (testing "contains sources switcher"
                          (is (re-find #"all" html)))
                 (testing "contains counter"
                          (is (re-find #"9999" html))))
               (done))))
