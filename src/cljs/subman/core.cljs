(ns subman.core
  (:require [subman.deps :refer [inject-for-production!]]
            [subman.components.core :refer [init-components]]
            [subman.handlers :as h]))

(defn ^:export run
  []
  (let [state (atom {:stable-search-query ""
                     :total-count 0})]
    (inject-for-production!)
    (h/handle-stable-search-query! state)
    (h/handle-total-count! state)
    (init-components state)))
