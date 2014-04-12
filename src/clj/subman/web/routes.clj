(ns subman.web.routes
  (:require [compojure.route :as route]
            [compojure.core :refer [defroutes GET]]
            [subman.models :as models]
            [subman.const :as const]
            [subman.web.api :as api]
            [subman.web.views :as views]
            [subman.web.push :as push]))

(defroutes main-routes
  (GET "/" [] (views/index-page))
  (GET "/search/*" [] (views/index-page))
  (GET "/api/search/" {params :params} (api/search params))
  (GET "/api/count/" {params :params} (api/total-count params))
  (GET "/api/list-languages/" {params :params} (api/list-languages params))
  (GET "/notifications/" [] push/notifications)
  (route/resources const/static-path))
