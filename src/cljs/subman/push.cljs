(ns subman.push
  (:require [cljs.reader :refer [read-string]]
            [goog.net.WebSocket]
            [goog.events :as gevents]
            [jayq.util :refer [log]]))

(defn create-push-connection
  "Create push connection"
  [total-count]
  (let [ws (goog.net.WebSocket.)]
    (gevents/listen ws goog.net.WebSocket.EventType.MESSAGE
                    (fn [msg] (->> (.-message msg)
                                   read-string
                                   :total-count
                                   (reset! total-count)))
                    false ws)
    (.open ws (str "ws://" (.-hostname js/location)
                   ":" (.-port js/location)
                   "/notifications/"))
    ws))


(defn init-push
  "Initiale push connection"
  [total-count]
  (try (create-push-connection total-count)
    (catch js/Error e (log e))))
