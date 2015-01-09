(ns subman.db
  (:require [clojure.set :refer [union]]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]]
            [clj-di.core :refer [register! get-dep]]
            [subman.helpers :refer [defsafe]]))

(defn connect!
  "Connect to databases"
  []
  (register! :db-connection (esr/connect (env :db-host)))
  (register! :raw-db-connection (mg/connect {:host (env :raw-db-host)
                                             :port (-> :raw-db-port env Integer.)})))

(defn get-raw-db
  []
  (mg/get-db (get-dep :raw-db-connection) (env :raw-db-name)))

(defsafe create-index!
  "Create database index for subtitles"
  []
  (esi/create (get-dep :db-connection)
              (env :index-name)
              :mappings {"subtitle"
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

(defsafe create-raw-index!
  []
  (let [raw-db (get-raw-db)]
    (mc/ensure-index raw-db "subtitle" (array-map :url 1) {:uniquer true})))

(defn init-db!
  "Init db connection and schema"
  []
  (connect!)
  (create-index!)
  (create-raw-index!))
