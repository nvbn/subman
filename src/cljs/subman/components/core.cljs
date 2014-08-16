(ns subman.components.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [jayq.core :refer [$]]
            [subman.helpers :refer [is-filled?]]
            [subman.components.search-input :refer [search-input]]
            [subman.components.search-result :refer [search-result]]
            [subman.components.welcome :refer [welcome]]))

(defn page
  "Component for whole page"
  [app owner]
  (om/component
   (dom/div {:className "page"}
            (om/build search-input app)
            (if (is-filled? (:search-query app))
              (om/build search-result app)
              (om/build welcome app)))))

(defn init-components
  [state]
  (om/root page state
           {:target (.get ($ :body) 0)}))
