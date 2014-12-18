(ns subman.migrations
  "Migration which should be run manually from repl."
  (:require [monger.collection :as mc]
            [clojurewerkz.elastisch.rest.document :as esd]
            [environ.core :refer [env]]
            [clj-di.core :refer [get-dep]]
            [subman.handlers :refer [init-models]]
            [subman.models :refer [get-raw-db get-total-count]]))

(defn from-index-to-raw-db!
  []
  (init-models)
  (let [raw-db (get-raw-db)
        total-count (get-total-count)
        limit 1000]
    (loop [offset 0]
      (let [subtitles (esd/search (get-dep :db-connection)
                                  (env :index-name) "subtitle"
                                  :from offset
                                  :size limit)]
        (doseq [item (->> subtitles :hits :hits (map :_source))]
          (mc/insert raw-db "subtitle" item))
        (when-not (> (+ offset limit) total-count)
          (println "Moved:" offset)
          (recur (+ offset limit)))))))
