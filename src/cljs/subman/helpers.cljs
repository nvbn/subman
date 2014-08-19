(ns subman.helpers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! chan]]
            [om.core :as om :include-macros true]
            [jayq.core :refer [$]]))

(defn is-filled?
  "Is field filled"
  [value]
  (and (not (nil? value))
       (not= "" value)))

(defn add-0-if-need
  "Add 0 before number if need"
  [number]
  (if (= (count (str number)) 1)
    (str "0" number)
    number))

(defn format-season-episode
  "Format episode numbers"
  [season episode]
  (str (when (is-filled? season)
         (str "S" (add-0-if-need season)))
       (when (is-filled? episode)
         (str "E" (add-0-if-need episode)))))

(defn render-node
  "Render component and return node"
  [component state]
  (let [result (chan)]
    (let [id (str "id-" (gensym))]
      (.append ($ :body) ($ (str "<div id='" id "'>")))
      (let [$el ($ (str "#" id))]
        (om/root (fn [app owner]
                   (go (>! result [owner $el]))
                   (component app owner))
                 state {:target (.get $el 0)})))
    result))

(defn value
  "Get value from event"
  [e]
  (.. e -target -value))

(defn atom-to-chan
  [atm]
  (let [ch (chan)]
    (add-watch atm (gensym)
               (fn [_ _ _ val]
                 (go (>! ch val))))
    ch))
