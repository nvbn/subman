(ns subman.components.search-input
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [timeout <!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [jayq.core :refer [$]]
            [subman.const :as const]
            [subman.autocomplete :refer [get-completion]]
            [subman.helpers :refer [value]]))

(defn completion-source
  "Source for typeahead autocompletion"
  [langs sources query cb]
  (cb (->> (get-completion query langs sources)
           (map #(js-obj "value" %))
           (take const/autocomplete-limit)
           (apply array))))

(defn handle-search-input
  "Update only stable search query"
  [app owner value]
  (om/update! app :search-query value)
  (go (om/set-state! owner :value value)
      (<! (timeout const/input-timeout))
      (when (= (om/get-state owner :value) value)
        (om/update! app :stable-search-query value))))

(defn search-input
  "Component for search input"
  [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/input #js {:onChange  #(handle-search-input app owner
                                                       (value %))
                      :value     (om/value (:search-query app))
                      :type      "text"
                      :className "search-input"}))
    om/IDidMount
    (did-mount [_]
      (let [input ($ (om/get-node owner))]
        (.typeahead input
                    #js {:highlight true}
                    #js {:source #(completion-source
                                   (get-in @app [:options :language :options])
                                   (get-in @app [:options :source :options])
                                   %1 %2)})
        (.on input "typeahead:closed"
             #(handle-search-input app owner (.val input)))))))
