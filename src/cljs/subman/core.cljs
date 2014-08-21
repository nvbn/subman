(ns subman.core
  (:require [alandipert.storage-atom :refer [local-storage]]
            [subman.const :as const]
            [subman.deps :refer [inject-for-production! sources]]
            [subman.components.core :refer [init-components]]
            [subman.handlers :as h]))

(defn ^:export run
  []
  (inject-for-production!)
  (let [state (atom {:stable-search-query ""
                     :total-count         0})
        options (local-storage (atom {:language const/default-language
                                      :source   (get @sources
                                                     const/default-type)})
                               :options)]
    (h/handle-stable-search-query! state options)
    (h/handle-total-count! state)
    (h/handle-options! state options)
    (init-components state)))
