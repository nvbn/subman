(ns subman.models
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]
            [subman.helpers :as helpers]
            [subman.const :as const]))

(def connection (atom nil))

(def total-count (atom 0))

(defn connect!
  "Connect to elastic"
  []
  (reset! connection
          (esr/connect const/db-host)))

(defn update-total-count
  "Update total count of subtitles"
  []
  (->> (esd/search @connection const/index-name "subtitle" :filter {})
       :hits
       :total
       (reset! total-count)))

(defn create-index
  "Create database index for subtitles"
  []
  (esi/create const/index-name :mappings {"subtitle"
                                          {:properties {:show {:type "string"}
                                                        :season {:type "string"
                                                                 :index "not_analyzed"}
                                                        :episode {:type "string"
                                                                  :index "not_analyzed"}
                                                        :name {:type "string"}
                                                        :lang {:type "string"}
                                                        :version {:type "string"}
                                                        :url {:type "string"
                                                              :index "not_analyzed"}
                                                        :source {:type "integer"}}}}))

(defn create-document
  "Put document into elastic"
  [doc]
  (esd/create @connection const/index-name "subtitle" doc))

(defn delete-all
  "Delete all documents"
  []
  (esd/delete-by-query-across-all-types @connection const/index-name (q/match-all)))

(defn- get-season-episode-parts
  "Get season episode parts"
  [text]
  (or (re-find #"[sS](\d+)[eE](\d+)" text)
      (re-find #"(\d+)[xX](\d+)" text)))

(defn- get-season-episode
  "Add season and episode filters"
  [text]
  (if-let [[_ season episode] (get-season-episode-parts text)]
    [(q/term :season (helpers/remove-first-0 season))
     (q/term :episode (helpers/remove-first-0 episode))]
    []))

(defn- get-source-filter
  "Get filter by source or blank vector"
  [source]
  (if (not= source const/type-all)
    [(q/term :source source)]
    []))

(defn- build-query
  "Build search query"
  [query lang source]
  (-> (let [prepared (clojure.string/replace query #"\." " ")]
        {:query (q/bool :must (concat [(q/fuzzy-like-this
                                        :like_text prepared
                                        :fields [:show :name]
                                        :boost const/show-name-boost)]
                                      (get-season-episode prepared)
                                      (get-source-filter source))
                        :should (q/fuzzy-like-this
                                 :like_text prepared
                                 :fields [:version]
                                 :boost const/version-boost))
         :filter (q/term :lang lang)
         :size const/result-size})
      vec
      flatten))

(defn search
  "Search for documents"
  [& {:keys [query offset lang source]}]
  (->> (apply esd/search @connection
              const/index-name
              "subtitle"
              :from offset
              (build-query query lang source))
       :hits
       :hits
       (map :_source)))

(defn in-db
  "Check subtitle already in db"
  [subtitle]
  (-> (let [url (:url subtitle)]
        (esd/search @connection
                    const/index-name
                    "subtitle"
                    :filter (q/term :url url)))
      :hits
      :total
      (> 0)))

(defn list-languages
  "List languages with count"
  []
  (-> (esd/search @connection
                  const/index-name
                  "subtitle"
                  :query (q/match-all)
                  :facets {:tag {:terms {:field "lang"
                                         :size const/languages-limit}}})
      :facets
      :tag
      :terms))
