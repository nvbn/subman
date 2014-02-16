(ns subman.api
  (:require [clojure.data.json :as json]
            [subman.models :as models]))

(defn- get-writer
  "Get writer from params"
  [params] (if (= (:format params) "json")
             json/write-str
             prn-str))

(defmacro defapi
  "Define api method"
  [name doc args & body] `(defn ~name ~args
                            ((get-writer (first ~args))
                             ~@body)))

(defapi search
  "Search for subtitles with params"
  [params] (let [query (:query params)
                 offset (get params :offset 0)
                 lang (get params :lang "english")]
              (models/search :query query
                             :offset offset
                             :lang lang)))

(defapi total-count
  "Get total subtitles count"
  [params] @models/total-count)
