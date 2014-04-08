(ns subman.t-components
  (:require-macros [purnam.test :refer [fact]])
  (:require [reagent.core :refer [atom]]
            [subman.helpers :refer [truthy]]
            [subman.const :as const]
            [subman.components :as components]))

(fact "search box should be ok"
      (components/search-box (atom "test")) => truthy)

(fact "result line should be ok"
      (components/result-line {:name "name"
                               :show "show"
                               :season "1"
                               :episode "2"
                               :source const/type-subscene
                               :lang "English"
                               :version "hd"}) => truthy)

(fact "result list should be ok"
      (components/result-list (atom "test")
                              (atom [])
                              (atom 1)
                              (atom 10)
                              (atom false)))
