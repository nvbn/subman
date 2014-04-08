(ns subman.web.style
  (:require [garden.def :refer [defstyles]]
            [garden.units :refer [px]]))

(defstyles main
  [:.search-input {:z-index 100}]
  [:.info-box {:text-align "center"}]
  [:.search-result-holder {:padding-left 0
                           :padding-right 0}]
  [:.search-result-list {:margin-top (px -5)}])
