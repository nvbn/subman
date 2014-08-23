(ns subman.components.edit-option
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [subman.helpers :refer [value]]))

(defn edit-option
  "Component for editing options"
  [option _]
  (reify
    om/IDisplayName
    (display-name [_] "Edit Option")
    om/IRender
    (render [_]
      (html [:select.edit-option.form-control
             {:value     (om/value (:value option))
              :on-change #(om/update! option :value (value %))}
             (let [vals (:options option)]
               (for [val (if (:is-sorted option)
                           (sort vals)
                           vals)]
                 [:option {:value (om/value val)
                           :key (str "option=" val)}
                  (om/value val)]))]))))
