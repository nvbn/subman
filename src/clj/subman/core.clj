(ns subman.core
  (:use [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.handler :as handler]
            [overtone.at-at :as at-at]
            [subman.routes :as routes]
            [subman.filler :as filler]
            [subman.const :as const]
            [subman.models :as models]))

(def pool (at-at/mk-pool))

(at-at/every const/update-period filler/update-all pool)

(try (models/create-index)
  (catch Exception e (println e)))

(def app
  (-> (handler/site routes/main-routes)
      (wrap-base-url)))
