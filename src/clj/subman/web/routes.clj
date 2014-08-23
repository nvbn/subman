(ns subman.web.routes
  (:require [compojure.route :as route]
            [compojure.core :refer [defroutes GET]]
            [subman.models :as models]
            [subman.const :as const]
            [subman.web.api :as api]
            [subman.web.views :as views]))

(defroutes main-routes
  (GET "/" [] (views/index-page))
  (GET "/search/*" [] (views/index-page))
  (GET "/api/search/" {params :params} {:body (api/search params)})
  (GET "/api/count/" [] {:body (api/total-count)})
  (GET "/api/list-languages/" [] {:body (api/list-languages)})
  (route/resources const/static-path))
