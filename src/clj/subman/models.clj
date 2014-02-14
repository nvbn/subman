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

(defn- get-season-episode
  "Add season and episode filters"
  [text] (if-let [nums (re-find #"[sS](\d+)[eE](\d+)" text)]
           [(q/term :season (helpers/remove-first-0 (get nums 1)))
            (q/term :episode (helpers/remove-first-0 (get nums 2)))]
           []))

(defn- build-query
  "Build search query"
  [query lang] (-> (let [prepared (clojure.string/replace query #"\." " ")]
                     {:query (q/bool :must (q/fuzzy-like-this :like_text prepared)
                                     :should (get-season-episode prepared))
                      :filter (q/term :lang lang)
                      :size const/result-size})
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
