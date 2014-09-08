(ns subman.handlers
  (:require [compojure.handler :as handler]
            [overtone.at-at :as at-at]
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.transit :refer [wrap-transit-response]]
            [subman.web.routes :as routes]
            [subman.parser.core :as parser]
            [subman.const :as const]
            [subman.models :as models]))

(def app (-> (handler/site routes/main-routes)
             (wrap-transit-response {:encoding :json})
             wrap-base-url
             wrap-reload))

(defn init-pool
  "Init pull for running periodic tasks"
  []
  (let [pool (at-at/mk-pool)]
    (at-at/every const/update-period
                 #(future (parser/load-new-subtitles))
                 pool)
    (at-at/every const/sitemap-period
                 #(future (models/update-unique-show-season-episode!))
                 pool)
    (at-at/every const/crawl-period
                 #(future (parser/load-all))
                 pool)))

(defn init-models
  "Init db connection and schema"
  []
  (models/connect!)
  (models/create-index!))

(defn init
  "Init ring handler"
  []
  (init-models)
  (future (init-pool)))
