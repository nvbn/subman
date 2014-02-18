(ns subman.t-routes
  (:require [midje.sweet :refer [fact => truthy]]
            [subman.routes :as routes]))

(fact "routes should be ok"
      routes/main-routes => truthy)
