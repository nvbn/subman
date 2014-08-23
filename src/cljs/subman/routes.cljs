(ns subman.routes
  (:require [secretary.core :as secretary :include-macros true :refer [defroute]]
            [goog.events :as gevents]
            [goog.history.EventType :as history-event]
            [subman.deps :as d]))

(defn set-search-query
  "Set value of stable search query"
  [value]
  (when-let [state (secretary/get-config :state)]
    (when (not= (:search-query @state) value)
      (swap! state assoc
             :stable-search-query value
             :search-query value)
      (swap! state update-in
             [:search-query-counter] inc))))

(defn change-url!
  "Change page url"
  [url]
  (when @d/history
    (.setToken @d/history url)))

(defn init-routes
  "Init history and spy to atom"
  [state]
  (when @d/history
    (secretary/set-config! :state state)
    (secretary/dispatch! (.getToken @d/history))
    (gevents/listen @d/history history-event/NAVIGATE
                    #(secretary/dispatch! (.-token %)))))

(defroute main-page "/" []
          (set-search-query ""))

(defroute search-page "/search/*query" {:keys [query]}
          (set-search-query query))
