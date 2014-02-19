(ns subman.filler
  (:require [subman.sources.addicted :as addicted]
            [subman.sources.podnapisi :as podnapisi]
            [subman.sources.opensubtitles :as opensubtitles]
            [subman.sources.subscene :as subscene]
            [subman.models :as models]
            [subman.const :as const]
            [subman.helpers :as helpers]))

(defn get-new-before
  "Get new subtitles before checker"
  [getter checker] (loop [page 1 results []]
                     (let [page-result (getter page)
                           new-result (remove checker page-result)]
                       (if (or (empty? new-result)
                               (> page const/update-deep))
                         results
                         (recur (inc page)
                                (concat new-result results))))))

(defn get-all-new
  "Get all new from callers with checker"
  [checker & callers] (mapcat #(get-new-before % checker) callers))

(defn update-all
  "Receive update from all sources"
  [] (->> (get-all-new models/in-db
                       subscene/get-release-page-result
                       opensubtitles/get-release-page-result
                       addicted/get-release-page-result
                       podnapisi/get-release-page-result)
          (map (helpers/make-safe models/create-document nil))
          (remove nil?)
          (map-indexed vector)
          (map (fn [[i item]]
                 (when (zero? (mod i 50))
                   (println (str "Updated " i)))
                 item))
          doall))
