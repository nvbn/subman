(ns subman.deps
  (:require [cljs-http.client :as http]
            [subman.const :as const]))

(def http-get (atom nil))

(def sources (atom nil))

(defn inject-for-production!
  "Inject dependencies for production usage."
  []
  (reset! http-get http/get)
  (reset! sources const/type-names))
