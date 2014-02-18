(ns subman.t-views
  (:require [midje.sweet :refer [fact truthy =>]]
            [subman.views :as views]))

(fact "index page should be ok"
      (views/index-page) => truthy)
