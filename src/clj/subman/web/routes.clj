(ns subman.web.routes
  (:require [compojure.route :as route]
            [compojure.core :refer [defroutes GET]]
            [ring.util.response :refer [content-type]]
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
  (GET "/sitemap.:n.xml" [n] (content-type
                               {:body (views/sitemap-page (Integer/parseInt n))}
                               "application/xml"))
  (GET "/robots.txt" [] (content-type {:body (views/robots-page)}
                                      "text/plain"))
  (route/resources const/static-path))
