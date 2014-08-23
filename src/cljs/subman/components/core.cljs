(ns subman.components.core
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [jayq.core :refer [$]]
            [subman.helpers :refer [is-filled?]]
            [subman.components.search-input :refer [search-input]]
            [subman.components.search-result :refer [search-result]]
            [subman.components.welcome :refer [welcome]]))

(defn page
  "Component for whole page"
  [app _]
  (reify
    om/IDisplayName
    (display-name [_] "Page")
    om/IRender
    (render [_]
      (html [:div.page
             (om/build search-input app)
             (if (is-filled? (:stable-search-query app))
               (om/build search-result app)
               (om/build welcome app))]))))


(defn init-components
  [state]
  (om/root page state
           {:target (.get ($ :body) 0)}))
