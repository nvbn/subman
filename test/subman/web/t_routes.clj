(ns subman.web.t-routes
  (:require [midje.sweet :refer [fact => truthy]]
            [subman.web.routes :as routes]))

(fact "routes should be ok"
      routes/main-routes => truthy)
