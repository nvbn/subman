(ns subman.api
  (:require [clojure.data.json :as json]
            [subman.models :as models]))

(defn search [params] (let [query (:query params)
                            offset (get params :offset 0)
                            lang (get params :lang "english")
                            writer (if (= (:format params) "json")
                                     json/write-str
                                     prn-str)]
                        (writer
                         (models/search :query query
                                        :offset offset
                                        :lang lang))))
