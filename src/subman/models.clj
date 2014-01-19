(ns subman.models
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]))

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

(defn search
  "Search for documents"
  [query] (->> (esd/search index "subtitle"
                           :query (q/fuzzy-like-this :like_text query)
                           :size 100)
               :hits
               :hits
               (map :_source)))
