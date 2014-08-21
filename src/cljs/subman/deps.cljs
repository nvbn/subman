(ns subman.deps
  (:require [goog.history.Html5History :as history5]
            [cljs-http.client :as http]
            [subman.const :as const]))

(def http-get (atom nil))

(def sources (atom nil))

(def history (atom nil))

(defn inject-for-production!
  "Inject dependencies for production usage."
  []
  (reset! http-get http/get)
  (reset! sources const/type-names)
  (when (history5/isSupported)
    (reset! history (doto (goog.history.Html5History.)
                      (.setUseFragment false)
                      (.setPathPrefix "")
                      (.setEnabled true)))))
