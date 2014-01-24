(ns subman.api
  (:require [clojure.data.json :as json]
            [subman.models :as models]))

(defn search [params] (let [query (:query params)]
                        (json/write-str
                          (models/search query))))
