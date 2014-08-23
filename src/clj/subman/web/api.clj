(ns subman.web.api
  (:require [subman.models :as models]
            [subman.const :as const]))

(defn- read-source
  [source]
  (if (string? source)
    (read-string source)
    source))

(defn search
  "Search for subtitles with params"
  [params]
  (let [query (:query params)
        offset (get params :offset 0)
        lang (get params :lang const/default-language)
        source (read-source
                 (get params :source (str const/default-type)))]
    (models/search :query query
                   :offset offset
                   :lang lang
                   :source source)))

(defn total-count
  "Get total subtitles count"
  []
  {:total-count (models/get-total-count)})

(defn list-languages
  "List all available languages with counts"
  []
  (models/list-languages))
