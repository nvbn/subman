(ns subman.routes
  (:require [compojure.route :as route]
            [compojure.core :refer [defroutes GET]]
            [subman.models :as models]
            [subman.api :as api]
            [subman.views :as views]
            [subman.push :as push]))

(defroutes main-routes
  (GET "/" [] (views/index-page))
  (GET "/search/*" [] (views/index-page))
  (GET "/api/search/" {params :params} (api/search params))
  (GET "/api/count/" {params :params} (api/total-count params))
  (GET "/notifications/" [] push/notifications)
  (route/resources "/"))
