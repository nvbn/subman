(ns subman.core
  (:require [reagent.core :as reagent]
            [subman.search :as search]))

(defn ^:export run []
  (reagent/render-component [search/search-page] (.-body js/document)))
