(ns subman.components.edit-option-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest done testing is]]
            [test-sugar.core :refer [is=]]
            [cljs.core.async :refer [<! timeout]]
            [om.core :as om :include-macros true]
            [jayq.core :refer [$]]
            [subman.const :as const]
            [subman.helpers :as h]
            [subman.components.edit-option :refer [edit-option]]))

(defn get-options
  "Get options texts from html"
  [$el]
  (map #(.html ($ %)) (.find $el "option")))

(deftest ^:async test-edit-option
  (go (let [state (atom {:value "first"
                         :options ["zero" "first" "second" "third"]
                         :is-sorted false})
            [owner $el] (<! (h/render-node edit-option state))
            select (.find $el ".edit-option")]
        (testing "set initial value"
          (is (= (.val select) "first")))
        (testing "values should be in initial order"
          (is (= ["zero" "first" "second" "third"]
                 (get-options $el))))
        (testing "update value on change"
          (.val select "zero")
          (h/simulate (om/get-node owner) :change)
          (is (= (:value @state) "zero")))
        (testing "sort values"
          (swap! state assoc-in [:is-sorted] true)
          (<! (timeout 1000)) ; wait for rerender
          (is (= ["first" "second" "third" "zero"]
                 (get-options $el))))
        (done))))
