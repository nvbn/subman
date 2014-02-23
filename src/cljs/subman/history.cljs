(ns subman.history
  (:require [cljs.reader :refer [read-string]]
            [goog.events :as gevents]
            [goog.history.EventType :as history-event]
            [goog.history.Html5History :as history5]
            [jayq.util :refer [wait]]))

(defn get-history
  "Get html5 history obj or fallback"
  []
  (doto (goog.history.Html5History.)
    (.setUseFragment false)
    (.setPathPrefix "/search/")
    (.setEnabled true)))

(defn init-history
  "Init history and spy to atom"
  [value]
  (when (history5/isSupported)
    (let [history (get-history)]
      (add-watch value :history
                 (fn [_ _ _ new-value]
                   (wait 500 #(when (= new-value @value)
                                (.setToken history new-value)))))
      (gevents/listen history history-event/NAVIGATE
                      #(let [token (-> % .-token js/decodeURIComponent)]
                         (when (and (= (.-isNavigatopn %))
                                    (not= token @value))
                           (reset! value token))))
      (->> (.getToken history)
           js/decodeURIComponent
           (reset! value)))))
