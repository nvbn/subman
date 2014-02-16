(ns subman.core
  (:gen-class)
  (:use [hiccup.middleware :only [wrap-base-url]]
        [ring.middleware.reload :only [wrap-reload]])
  (:require [compojure.handler :as handler]
            [overtone.at-at :as at-at]
            [org.httpkit.server :as server]
            [subman.routes :as routes]
            [subman.filler :as filler]
            [subman.const :as const]
            [subman.models :as models]))

(def app (-> (handler/site routes/main-routes)
             wrap-base-url
             wrap-reload))

(def pool (at-at/mk-pool))

(defn -main [& args]
  (try (models/create-index)
    (catch Exception e (println e)))
  (models/update-total-count)
  (at-at/every const/update-period
               (fn [] filler/update-all
                 models/update-total-count)
               pool)
  (let [port (Integer/parseInt
              (or (System/getenv "PORT") const/default-port))]
    (server/run-server app {:port port})))
