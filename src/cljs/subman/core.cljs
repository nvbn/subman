(ns subman.core
  (:require [subman.deps :refer [inject-for-production!]]
            [subman.components.core :refer [init-components]]))

(defn ^:export run
  []
  (let [state (atom {})]
    (inject-for-production!)
    (init-components state)))
