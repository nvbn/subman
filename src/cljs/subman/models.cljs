(ns subman.models
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.reader :refer [read-string]]
            [subman.deps :refer [http-get]]
            [cljs.core.async :refer [<!]]
            [subman.const :as const]))

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
  (get (apply merge
              (map (fn [el]
                     {(-> el val str string/lower-case)
                       (key el)})
                   const/type-names))
       (string/lower-case source)
       const/type-none))

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

(defn create-search-url
  "Create search url from query"
  [query offset lang source]
  (-> ["/api/search/?" query]
      lang-query-part
      (set-default-part "lang" lang)
      source-query-part
      (set-default-part "source" (get-source-id source))
      (query-offset-query-part offset)))

(defn read-respone
  "Read response from server"
  [response]
  (read-string (:body response)))

(defn get-search-result
  "Get search result from query"
  [query offset lang source]
  (go (-> (create-search-url query offset lang source)
          (@http-get)
          <!
          read-respone)))

(defn get-total-count
  "Get total count of indexed subtitles"
  []
  (go (-> (@http-get "/api/count/")
          <!
          read-respone)))
