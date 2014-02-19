(ns subman.t-components
  (:require-macros [purnam.test.sweet :refer [fact]])
  (:require [reagent.core :refer [atom]]
            [subman.helpers :refer [truthy]]
            [subman.const :as const]
            [subman.components :as components]))

(fact "search box should be ok"
      (components/search-box {:value (atom "test")}) => truthy)

(fact "result line should be ok"
      (components/result-line {:name "name"
                               :show "show"
                               :season "1"
                               :episode "2"
                               :source const/type-subscene
                               :lang "English"
                               :version "hd"}) => truthy)

(fact "result list should be ok"
      (components/result-list {:query (atom "test")
                               :items (atom [])
                               :counter (atom 1)
                               :totla-count (atom 10)}))
