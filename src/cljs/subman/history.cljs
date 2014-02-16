(ns subman.history
  (:require [cljs.reader :refer [read-string]]
            [goog.events :as gevents]
            [goog.history.EventType :as history-event]
            [goog.string :as string]
            [goog.history.Html5History :as history5]))

(defn get-history
  "Get html5 history obj or fallback"
  [] (let [history (doto (goog.history.Html5History.)
                     (.setUseFragment false)
                     (.setPathPrefix "/search/")
                     (.setEnabled true))]
       (gevents/unlisten (.-window_ history) (.-POPSTATE gevents/EventType)
                         (.-onHistoryEvent_ history), false, history)
       history))

(defn init-history
  "Init history and spy to atom"
  [value] (when (history5/isSupported)
            (let [history (get-history)]
              (add-watch value :history
                         (fn [key ref old-value new-value]
                           (.setToken history new-value)))
              (gevents/listen history history-event/NAVIGATE
                              #(let [token (.-token %)]
                                 (when-not (= token @value)
                                   (reset! value (.-token %)))))
              (->> (.getToken history)
                   string/urlDecode
                   (reset! value)))))
