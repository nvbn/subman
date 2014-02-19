(ns subman.t-history
  (:require-macros [purnam.test.sweet :refer [fact]])
  (:require [reagent.core :refer [atom]]
            [subman.helpers :refer [truthy]]
            [subman.history :as history]))

(fact "should get history"
      (history/get-history) => truthy)

(fact "should init history"
      (history/init-history (atom "test")) => truthy)
