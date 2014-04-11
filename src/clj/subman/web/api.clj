(ns subman.web.api
  (:require [clojure.data.json :as json]
            [subman.models :as models]
            [subman.const :as const]))

(defn- get-writer
  "Get writer from params"
  [params]
  (if (= (:format params) "json")
    json/write-str
    prn-str))

(defmacro defapi
  "Define api method"
  [name doc args & body]
  `(defn ~name ~args
     ((get-writer (first ~args))
      ~@body)))

(defn- read-source
  [source]
  (if (string? source)
    (read-string source)
    source))

(defapi search
  "Search for subtitles with params"
  [params]
  (let [query (:query params)
        offset (get params :offset 0)
        lang (get params :lang "english")
        source (read-source
                (get params :source (str const/type-all)))]
    (models/search :query query
                   :offset offset
                   :lang lang
                   :source source)))

(defapi total-count
  "Get total subtitles count"
  [params]
  @models/total-count)
