(ns subman.api
  (:require [clojure.data.json :as json]
            [subman.models :as models]))

(defn search [params] (let [query (:query params)
                            offset (get params :offset 0)]
                        (json/write-str
                         (models/search :query query
                                        :offset offset))))

(search {:query "cat"})
