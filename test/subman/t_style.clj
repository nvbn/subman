(ns subman.t-style
  (:require [midje.sweet :refer [fact => truthy]]
            [subman.style :as style]))

(fact "style should be ok"
      style/main => truthy)
