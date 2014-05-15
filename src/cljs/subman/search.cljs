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
            [subman.const :as const]
            [subman.options :as options]))

(defn ?-query-part
  [part-name part-formatter [api-query query]]
  (let [pattern (re-pattern (str ":" part-name " (\\w*)"))
        search-result (re-find pattern query)]
    (if search-result
      (let [value (last search-result)
            query (string/replace query (first search-result) "")]
        [(str api-query part-name "="
              (part-formatter value) "&")
         query])
      [api-query query])))

(defn set-default-part
  "Set default query part value if need"
  [[api-query query] name default]
  (if (re-find (re-pattern (str name "=")) api-query)
    [api-query query]
    [(str api-query name "=" default "&")
     query]))

(defn lang-query-part
  "Get lang query part"
  [param]
  (?-query-part "lang" identity param))

(defn get-source-id
  "Get identifier of source from name"
  [source]
  (let [prepared (string/lower-case source)]
    (get (apply merge
                (map (fn [el]
                       {(-> el val str string/lower-case)
                        (key el)})
                     const/type-names))
         (string/lower-case source)
         const/type-none)))

(defn source-query-part
  "Get source query part"
  [param]
  (?-query-part "source"
                #(get-source-id %)
                param))

(defn query-offset-query-part
  "Query and offset query part"
  [[api-query query] offset]
  (str api-query "query=" (string/trim query) "&offset=" offset))

(defn create-search-request
  "Create search request from query"
  [query offset props]
  (-> ["/api/search/?" query]
      lang-query-part
      (set-default-part "lang" @(:current-language props))
      source-query-part
      (set-default-part "source" (get-source-id @(:current-source props)))
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

(defn update-title
  "Update coument title"
  [query]
  (set! (.-title js/document)
        (if (= 0 (count query))
          "Subman - subtitle search service"
          (str "Subman - " query))))

(defn watch-to-query
  "Watch to search query"
  [query results counter offset in-progress props]
  (add-watch query :search-request
             (fn [_ _ _ new-value]
               (update-title new-value)
               (reset! offset 0)
               (reset! in-progress true)
               (update-result (create-search-request new-value @offset props)
                              results
                              counter
                              (fn [value]
                                (reset! in-progress false)
                                (reset! results value))))))

(defn watch-to-scroll
  "Watch to scroll"
  [query results counter offset props]
  (.scroll ($ js/window) #(when (and (= (- (-> js/document $ .height)
                                           (-> js/window $ .height))
                                        (-> js/window $ .scrollTop))
                                     (= (count @results) const/result-size))
                            (swap! offset + const/result-size)))
  (add-watch offset :scroll
             (fn [_ _ _ new-value]
               (update-result (create-search-request @query new-value props)
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

(defn force-update-on
  "Fix slow updating in firefox"
  [query atoms]
  (doseq [one atoms]
    (add-watch one :force-update
               (fn [_ _ _ _]
                 (reset! query @query)))))

(defn search-controller
  "Search page controller"
  []
  (let [query (atom "")
        results (atom [])
        counter (atom 0)
        total-count (atom 0)
        offset (atom 0)
        in-progress (atom false)
        storage (options/get-local-storage)
        props {:languages (options/get-languages)
               :current-language (options/get-language-option storage)
               :sources (options/get-sources)
               :current-source (options/get-source-option storage)}]
    (force-update-on query (vals props))
    (watch-to-query query results counter offset in-progress props)
    (watch-to-scroll query results counter offset props)
    (update-total-count total-count)
    (init-history query)
    (init-push total-count)
    (wait 0 #(init-autocomplete query
                                (:languages props)
                                (:sources props)))
    (wait 0 set-focus)
    (render-component [components/search-page
                       query
                       results
                       counter
                       total-count
                       in-progress
                       props]
                      (.-body js/document))))
