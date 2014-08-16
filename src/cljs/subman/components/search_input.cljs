(ns subman.components.search-input
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [timeout <!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [subman.const :as const]
            [subman.helpers :refer [value]]))

(defn handle-search-input
  "Update only stable search query"
  [app owner value]
  (go (om/set-state! owner :value value)
      (<! (timeout const/input-timeout))
      (when (= (om/get-state owner :value) value)
        (om/update! app :search-query value))))

(defn search-input
  "Component for search input"
  [app owner]
  (om/component
   (dom/input #js {:onChange #(handle-search-input app owner
                                                   (value %))
                   :value (om/value (:search-query app))
                   :type "text"
                   :className "search-input"})))
