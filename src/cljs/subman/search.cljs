(ns subman.search
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.reader :refer [read-string]]
            [reagent.core :refer [atom]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [jayq.core :refer [$]]
            [jayq.util :refer [wait]]
            [subman.history :refer [init-history]]
            [subman.push :refer [init-push]]
            [subman.components :as components]))

(defn create-search-request
  "Create search request from query"
  [query]
  (str "/api/search/" (if (re-find #" :lang " query)
                        (let [parts (string/split query #" :lang ")]
                          (str "?query=" (get parts 0) "&lang=" (get parts 1)))
                        (str "?query=" query))))

(defn watch-to-query
  "Watch to search query"
  [query results counter]
  (add-watch query :search-request
             (fn [_ _ _ new-value]
               (let [current (swap! counter inc)
                     url (create-search-request new-value)]
                 (go (let [response (<! (http/get url))]
                       (when (= current @counter)
                         (->> (:body response)
                              read-string
                              (reset! results)))))))))

(defn update-total-count
  "Update total count value"
  [total-count]
  (go (->> (http/get "/api/count/")
           <!
           :body
           read-string
           (reset! total-count))))

(defn set-focus
  "Set focus after timeout"
  []
  (wait 0 #(.focus ($ "#search-input"))))

(defn search-page
  "Search page view"
  []
  (let [query (atom "")
        results (atom [])
        counter (atom 0)
        total-count (atom 0)]
    (watch-to-query query results counter)
    (update-total-count total-count)
    (init-history query)
    (init-push total-count)
    (set-focus)
    [:div [components/search-box {:value query}]
     [components/result-list {:items results
                              :query query
                              :counter counter
                              :total-count total-count}]]))
