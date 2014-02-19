(ns subman.core
  (:gen-class)
  (:require [compojure.handler :as handler]
            [overtone.at-at :as at-at]
            [org.httpkit.server :as server]
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
                          (models/update-total-count)
                          (println "update finished")))
                 pool)))

(defn -main
  "Run application"
  [& args]
  (models/connect!)
  (try (models/create-index)
    (catch Exception e (println e)))
  (models/update-total-count)
  (future (init-pool))
  (let [port (Integer/parseInt
              (or (System/getenv "PORT") const/default-port))]
    (server/run-server app {:port port})))
