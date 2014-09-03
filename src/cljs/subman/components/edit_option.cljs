(ns subman.components.edit-option
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [subman.helpers :refer [value]]))

(defcomponent edit-option [option _]
  (display-name [_] "Edit Option")
  (render [_] (html [:select.edit-option.form-control
                     {:value (:value option)
                      :on-change #(om/update! option :value (value %))}
                     (let [vals (:options option)]
                       (for [val (if (:is-sorted option)
                                   (sort vals)
                                   vals)]
                         [:option {:value val
                                   :key (str "option=" val)}
                          val]))])))
