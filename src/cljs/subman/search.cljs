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
            [subman.autocomplete :refer [init-autocomplete]]
            [subman.const :as const]))

(defn ?-query-part
  [part-name part-formatter [api-query query]]
  (let [pattern (re-pattern (str " :" part-name " (\\w*)"))
        search-result (re-find pattern query)]
    (if search-result
      (let [value (last search-result)
            query (string/replace query (first search-result) "")]
        [(str api-query part-name "="
              (part-formatter value) "&")
         query])
      [api-query query])))

(defn lang-query-part
  "Get lang query part"
  [param]
  (?-query-part "lang" identity param))

(defn get-source-id
  "Get identifier of source from name"
  [source]
  (let [prepared (string/lower-case source)]
    (if (= prepared "all")
      -1
      (get (apply merge
                  (map (fn [el]
                         {(-> el val str string/lower-case)
                          (key el)})
                       const/type-names))
           (string/lower-case source)
           const/type-none))))

(defn source-query-part
  "Get source query part"
  [param]
  (?-query-part "source"
                get-source-id
                param))

(defn query-offset-query-part
  "Query and offset query part"
  [[api-query query] offset]
  (str api-query "query=" query "&offset=" offset))

(defn create-search-request
  "Create search request from query"
  [query offset]
  (-> ["/api/search/?" query]
      lang-query-part
      source-query-part
      (query-offset-query-part offset)))

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
  [query results counter offset in-progress]
  (add-watch query :search-request
             (fn [_ _ _ new-value]
               (reset! offset 0)
               (reset! in-progress true)
               (update-result (create-search-request new-value @offset)
                              results
                              counter
                              (fn [value]
                                (reset! in-progress false)
                                (reset! results value))))))

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
        offset (atom 0)
        in-progress (atom false)]
    (watch-to-query query results counter offset in-progress)
    (watch-to-scroll query results counter offset)
    (update-total-count total-count)
    (init-history query)
    (init-push total-count)
    (set-focus)
    (wait 0 #(init-autocomplete query))
    (render-component [components/search-page
                       query
                       results
                       counter
                       total-count
                       in-progress]
                      (.-body js/document))))
