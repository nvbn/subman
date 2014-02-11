(ns subman.core
  (:use compojure.core [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.handler :as handler]
            [subman.routes :as routes]))

(def app
  (-> (handler/site routes/main-routes)
      (wrap-base-url)))
