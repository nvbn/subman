(ns subman.core
  (:require [alandipert.storage-atom :refer [local-storage]]
            [cljs-http.client :as http]
            [clj-di.core :refer [register! get-dep]]
            [goog.history.Html5History :as history5]
            [subman.const :as const]
            [subman.components.core :refer [init-components]]
            [subman.routes :refer [init-routes]]
            [subman.handlers :as h]))

(defn ^:export run
  []
  (register! :http-get http/get
             :sources const/type-names)
  (when (history5/isSupported)
    (register! :history (doto (goog.history.Html5History.)
                          (.setUseFragment false)
                          (.setPathPrefix "")
                          (.setEnabled true))))
  (let [state (atom {:stable-search-query ""
                     :search-query ""
                     :total-count 0})
        options (local-storage (atom {:language const/default-language
                                      :source (get (get-dep :sources)
                                                   const/default-type)})
                               :options)]
    (init-routes state)
    (h/handle-search-query! state)
    (h/handle-stable-search-query! state options)
    (h/handle-total-count! state)
    (h/handle-options! state options)
    (init-components state)))
