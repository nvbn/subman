(ns subman.search
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.reader :refer [read-string]]
            [reagent.core :refer [atom render-component]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [jayq.core :refer [$]]
            [jayq.util :refer [wait]]
            [subman.history :refer [init-history]]
            [subman.push :refer [init-push]]
            [subman.components :as components]
            [subman.const :as const]))

(defn create-search-request
  "Create search request from query"
  [query offset]
  (str "/api/search/" (if (re-find #" :lang " query)
                        (let [parts (string/split query #" :lang ")]
                          (str "?query=" (get parts 0) "&lang=" (get parts 1)))
                        (str "?query=" query)) "&offset=" offset))

(defn update-result
  "Update search result"
  [url results counter than]
  (let [current (swap! counter inc)]
    (go (let [response (<! (http/get url))]
          (when (= current @counter)
            (->> (:body response)
                 read-string
                 than))))))

(defn watch-to-query
  "Watch to search query"
  [query results counter offset]
  (add-watch query :search-request
             (fn [_ _ _ new-value]
               (reset! offset 0)
               (update-result (create-search-request new-value @offset)
                              results
                              counter
                              #(reset! results %)))))

(defn watch-to-scroll
  "Watch to scroll"
  [query results counter offset]
  (.scroll ($ js/window) #(when (and (= (- (-> js/document $ .height)
                                           (-> js/window $ .height))
                                        (-> js/window $ .scrollTop))
                                     (= (count @results) const/result-size))
                         (swap! offset + const/result-size)))
  (add-watch offset :scroll
             (fn [_ _ _ new-value]
               (update-result (create-search-request @query new-value)
                              results
                              counter
                              #(swap! results concat %)))))

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

(defn search-controller
  "Search page controller"
  []
  (let [query (atom "")
        results (atom [])
        counter (atom 0)
        total-count (atom 0)
        offset (atom 0)]
    (watch-to-query query results counter offset)
    (watch-to-scroll query results counter offset)
    (update-total-count total-count)
    (init-history query)
    (init-push total-count)
    (set-focus)
    (render-component [components/search-page query results counter total-count]
                      (.-body js/document))))
