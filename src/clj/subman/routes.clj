(ns subman.routes
  (:use [compojure.core :only [defroutes GET]])
  (:require [compojure.route :as route]
            [subman.models :as models]
            [subman.api :as api]
            [subman.views :as views]))

(defroutes main-routes
  (GET "/" [] (views/index-page))
  (GET "/api/search/" {params :params} (api/search params))
  (route/resources "/")
  (route/not-found (views/index-page)))
