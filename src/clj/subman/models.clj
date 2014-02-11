(ns subman.models
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]
            [subman.helpers :as helpers]
            [subman.const :as const]))

(esr/connect! const/db-host)

(defn create-index []
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
  [doc] (esd/create const/index-name "subtitle" doc))

(defn delete-all
  "Delete all documents"
  [] (esd/delete-by-query-across-all-types const/index-name (q/match-all)))

(defn- remove-dots
  "Remove dots from query"
  [query] (clojure.string/replace query #"\." " "))

(defn- build-fuzzy
  "Build default fuzzy query"
  [query] {:query (q/fuzzy-like-this :like_text query)})


(defn- add-season-episode
  "Add season and episode filters"
  [query text] (if-let [nums (re-find #"[sS](\d+)[eE](\d+)" text)]
                 (assoc query :season (helpers/remove-first-0 (get nums 1))
                   :episode (helpers/remove-first-0 (get nums 2)))
                 query))

(defn- build-filters
  "Build filters for search"
  [text lang] {:term (add-season-episode {:lang lang}
                                          text)})

(defn- build-query
  "Build search query"
  [query lang] (-> (remove-dots query)
              build-fuzzy
              (assoc :filter (build-filters query lang))
              (assoc :size 100)
              vec
              flatten))

(defn search
  "Search for documents"
  [& {:keys [query offset lang]}] (->> (apply esd/search const/index-name
                                              "subtitle"
                                              :from offset
                                              (build-query query lang))
                                       :hits
                                       :hits
                                       (map :_source)))

(defn in-db
  "Check subtitle already in db"
  [subtitle] (-> (let [url (:url subtitle)]
                   (esd/search const/index-name
                               "subtitle"
                               :filter {:term {:url url}}))
                 :hits
                 :total
                 (> 0)))
