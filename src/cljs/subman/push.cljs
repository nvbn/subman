(ns subman.push
  (:use [cljs.reader :only [read-string]])
  (:require [goog.net.WebSocket]
            [goog.events :as gevents]))

(defn init-push
  "Init push connection"
  [total-count] (let [ws (goog.net.WebSocket.)]
                  (gevents/listen ws goog.net.WebSocket.EventType.MESSAGE
                                  (fn [msg] (->> (.-message msg)
                                                 read-string
                                                 :total-count
                                                 (reset! total-count)))
                                  false ws)
                  (.open ws (str "ws://" (.-hostname js/location)
                                 ":" (.-port js/location)
                                 "/notifications/"))))
