(ns subman.core
  (:require [reagent.core :as reagent]
            [subman.search :as search]))

(defn ^:export run
  "Render base page component"
  []
  (reagent/render-component [search/search-page] (.-body js/document)))
