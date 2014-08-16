(ns subman.core
  (:require [subman.components.core :refer [init-components]]))

(defn ^:export run
  []
  (let [state (atom {})]
    (init-components state)))
