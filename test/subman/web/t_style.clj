(ns subman.web.t-style
  (:require [midje.sweet :refer [fact => truthy]]
            [subman.web.style :as style]))

(fact "style should be ok"
      style/main => truthy)
