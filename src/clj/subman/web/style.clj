(ns subman.web.style
  (:require [garden.def :refer [defstyles]]
            [garden.units :refer [px percent]]))

(defstyles main
  [:.search-input {:z-index 100
                   :background-color "#fff"}]
  [:.info-box {:text-align "center"}]
  [:.search-result-holder {:padding-left 0
                           :padding-right 0}]
  [:.search-result-list {:margin-top (px -5)}]
  [:.twitter-typeahead {:width (percent 100)}]
  [:#search-input {:height (px 46)
                   :font-size (px 18)
                   :line-height 1.33
                   :margin-bottom (px -5)}])
