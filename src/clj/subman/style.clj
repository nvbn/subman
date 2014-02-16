(ns subman.style
  (:require [garden.def :refer [defstyles]]))

(defstyles main
  [:.search-input {:z-index 100}]
  [:.info-box {:text-align "center"}])
