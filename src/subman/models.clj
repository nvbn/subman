(ns subman.models
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]
            [subman.helpers :as helpers]))

(esr/connect! "http://127.0.0.1:9200")

(def index "subman1")

(defn create-index [i-name]
  (esi/create i-name :mappings {"subtitle"
                                {:properties {
                                              :show {:type "string"}
                                              :season {:type "string"}
                                              :episode {:type "string"}
                                              :name {:type "string"}
                                              :lang {:type "string"}
                                              :version {:type "string"}
                                              :url {:type "string"}}}}))

(defn create-document
  "Put document into elastic"
  [doc] (esd/create index "subtitle" doc))

(defn delete-all
  "Delete all documents"
  [] (esd/delete-by-query-across-all-types index (q/match-all)))

(defn- remove-dots
  "Remove dots from query"
  [query] (clojure.string/replace query #"\." " "))

(defn- build-fuzzy
  "Build default fuzzy query"
  [query] {:query (q/fuzzy-like-this :like_text query)})


(defn- add-season-episode
  "Add season and episode filters"
  [query text] (if-let [nums (re-find #"[sS](\d+)[eE](\d+)" text)]
                 (assoc query :filter {:term {:season (helpers/remove-first-0 (get nums 1))
                                              :episode (helpers/remove-first-0 (get nums 2))}})
                 query))

(defn- build-query
  "Build search query"
  [query] (-> (remove-dots query)
              build-fuzzy
              (add-season-episode query)
              (assoc :size 100)
              vec
              flatten))

(defn search
  "Search for documents"
  [query] (->> (apply esd/search index "subtitle" (build-query query))
               :hits
               :hits
               (map :_source)))
