(ns subman.handlers
  (:require-macros [cljs.core.async.macros :refer [go-loop go]])
  (:require [cljs.core.async :refer [<! alts!]]
            [subman.helpers :refer [subscribe-to-state]]
            [subman.models :as m]))

(defn handle-stable-search-query!
  "Update search result when stable query changed"
  [state options]
  (let [ch (subscribe-to-state state :stable-search-query)]
    (go-loop []
             (let [val (<! ch)]
               (swap! state assoc
                      :in-progress true)
               (swap! state assoc
                      :results (<! (m/get-search-result val 0
                                                        (:language @options)
                                                        (:source @options)))
                      :offset 0
                      :in-progress false)
               (recur)))))

(defn handle-total-count!
  "Update total count value on start"
  [state]
  (go (swap! state assoc
             :total-count (<! (m/get-total-count)))))

(defn handle-single-option!
  "Bind options in state and options atom"
  [state options option]
  (let [options-ch (subscribe-to-state options option)
        state-ch (subscribe-to-state state :options option :value)]
    (go-loop []
             (let [[val ch] (alts! [options-ch state-ch])]
               (when-not (= val "")
                 (condp = ch
                   state-ch (swap! options assoc
                                   option val)
                   options-ch (swap! state assoc-in
                                     [:options option :value] val)))
               (recur)))))

(defn handle-options!
  "Init options and bind"
  [state options]
  (go (swap! state assoc
             :options {:language {:options   (<! (m/get-languages))
                                  :value     (:language @options)
                                  :is-sorted true}
                       :source   {:options (<! (m/get-sources))
                                  :value   (:source @options)}})
      (handle-single-option! state options :language)
      (handle-single-option! state options :source)))
