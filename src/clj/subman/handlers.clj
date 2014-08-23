(ns subman.handlers
  (:require [compojure.handler :as handler]
            [overtone.at-at :as at-at]
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.reload :refer [wrap-reload]]
            [subman.web.routes :as routes]
            [subman.filler :as filler]
            [subman.const :as const]
            [subman.models :as models]))

(def app (-> (handler/site routes/main-routes)
             wrap-base-url
             wrap-reload))

(defn init-pool
  "Init pull for running periodic tasks"
  []
  (let [pool (at-at/mk-pool)]
    (at-at/every const/update-period
                 (fn [] (future (println "start update")
                          (filler/update-all)
                          (println "update finished")))
                 pool)))

(defn init-models
  "Init db connection and schema"
  []
  (models/connect!)
  (try (models/create-index)
    (catch Exception e)))

(defn init
  "Init ring handler"
  []
  (init-models)
  (future (init-pool)))
