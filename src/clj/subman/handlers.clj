(ns subman.handlers
  (:require [compojure.handler :as handler]
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.transit :refer [wrap-transit-response wrap-transit-body]]
            [subman.web.routes :as routes]
            [subman.models :as models]))

(def app (-> (handler/site routes/main-routes)
             (wrap-transit-response {:encoding :json})
             (wrap-transit-body {:encoding :json})
             wrap-base-url
             wrap-reload))

(defn init-models
  "Init db connection and schema"
  []
  (models/connect!)
  (models/create-index!)
  (models/create-raw-index!))

(defn init
  "Init ring handler"
  []
  (init-models))
