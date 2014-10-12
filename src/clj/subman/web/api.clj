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
  [{:keys [query offset lang source limit] :or {offset 0
                                                lang const/default-language
                                                source (str const/default-type)
                                                limit const/result-size}}]
  (models/search :query query
                 :offset offset
                 :lang lang
                 :source (read-source source)
                 :limit limit))

(defn bulk-search
  "Bulk search reuqyest, equals to many calls of `seach`."
  [{:keys [queries offset lang source limit] :or {offset 0
                                                  lang const/default-language
                                                  source (str const/default-type)
                                                  limit const/result-size}}]
  (into {} (pmap (fn [query]
                   [query (search {:query query
                                   :offset offset
                                   :lang lang
                                   :source source
                                   :limit limit})])
                 queries)))

(defn total-count
  "Get total subtitles count"
  []
  {:total-count (models/get-total-count)})

(defn list-languages
  "List all available languages with counts"
  []
  (models/list-languages))

(defn list-sources
  "List all available sources with names."
  []
  const/type-names)
