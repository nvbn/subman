(ns subman.routes
  (:use compojure.core
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [subman.models :as models]
            [subman.api :as api]
            [subman.views :as views]))

(defroutes main-routes
  (GET "/" [] (views/index-page))
  (GET "/api/search/" {params :params} (api/search params))
  (route/resources "/"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))
