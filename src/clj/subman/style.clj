(ns subman.style
  (:use [garden.def :only [defstyles]]))

(defstyles main
  [:.search-input {:z-index 100}]
  [:.info-box {:text-align "center"}])
