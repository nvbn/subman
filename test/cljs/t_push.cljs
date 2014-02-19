(ns subman.t-push
  (:require-macros [purnam.test.sweet :refer [fact]])
  (:require [reagent.core :refer [atom]]
            [subman.helpers :refer [truthy]]
            [subman.push :as push]))

(fact "init push should be ok"
      (push/init-push (atom 100)) => truthy)
