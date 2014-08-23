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

(defn icon-part
  [app]
  (dom/span #js {:className "input-group-addon no-border-radius"}
            (if (= "" (:search-query app))
              (dom/i #js {:className "fa fa-search"})
              (dom/a #js {:onClick   (fn [e]
                                       (.preventDefault e)
                                       (om/update! app
                                                  :search-query ""))
                          :href      "#"
                          :className "clear-input-btn"}
                     (dom/i #js {:className "fa fa-chevron-left"})))))

(defn input-field-part
  [app]
  (dom/input #js {:onChange    #(om/update! app
                                            :search-query (value %))
                  :value       (om/value (:search-query app))
                  :placeholder "Type search query"
                  :type        "text"
                  :className   "search-input form-control no-border-radius"}))

(defn search-input
  "Component for search input"
  [app owner]
  (reify
    om/IDisplayName
    (display-name [_] "Search Input")
    om/IRender
    (render [_]
      (dom/div #js {:data-spy        "affix"
                    :data-offset-top "40"
                    :className       "input-group input-group-lg col-xs-12 search-input-box"}
               (icon-part app)
               (input-field-part app)))
    om/IDidMount
    (did-mount [_]
      (let [input (.find ($ (om/get-node owner))
                         "input.search-input")]
        (.typeahead input
                    #js {:highlight true}
                    #js {:source #(completion-source
                                   (get-in @app [:options :language :options])
                                   (get-in @app [:options :source :options])
                                   %1 %2)})
        (.on input "typeahead:closed"
             #(om/update! app
                          :search-query (.val input)))
        (.focus input)
        (.select input)))))
