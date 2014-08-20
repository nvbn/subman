(ns subman.handlers
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<!]]
            [subman.helpers :refer [subscribe-to-state]]
            [subman.models :as m]))

(defn handle-stable-search-query!
  "Update search result when stable query changed"
  [state]
  (let [ch (subscribe-to-state state :stable-search-query)]
    (go-loop []
             (let [val (<! ch)]
               (swap! state assoc
                      :in-progress true)
               (swap! state assoc
                      :results (<! (m/get-search-result val 0
                                                        "english" "all"))
                      :offset 0
                      :in-progress false)
               (recur)))))
