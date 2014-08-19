(ns subman.deps
  (:require [cljs-http.client :as http]))

(def http-get (atom nil))

(defn inject-for-production!
  "Inject dependencies for production usage."
  []
  (reset! http-get http/get))
