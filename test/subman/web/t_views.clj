(ns subman.web.t-views
  (:require [midje.sweet :refer [fact truthy =>]]
            [subman.web.views :as views]))

(fact "index page should be ok"
      (views/index-page) => truthy)
