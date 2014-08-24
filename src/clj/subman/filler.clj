(ns subman.filler
  (:require [clojure.core.async :as async :refer [<!! >!]]
            [subman.sources.addicted :as addicted]
            [subman.sources.podnapisi :as podnapisi]
            [subman.sources.opensubtitles :as opensubtitles]
            [subman.sources.subscene :as subscene]
            [subman.sources.notabenoid :as notabenoid]
            [subman.sources.uksubtitles :as uksubtitles]
            [subman.models :as models]
            [subman.const :as const]
            [subman.helpers :as helpers]))

(defn- get-new-for-page
  "Get new subtitles for page"
  [getter checker page]
  (remove checker
          (getter page)))

(defn get-new-subtitles-in-chan
  "Get new result from pages in chan"
  [getter checker]
  (let [result (async/chan)]
    (async/thread
      (async/go-loop [page 1]
                     (when (<= page const/update-deep)
                       (if-let [page-result (seq (get-new-for-page getter
                                                                   checker page))]
                         (do (doseq [subtitle page-result]
                               (>! result subtitle))
                             (recur (inc page)))
                         (async/close! result)))))
    result))

(defn update-all
  "Receive update from all sources"
  []
  (let [ch (async/merge (map #(get-new-subtitles-in-chan % models/in-db)
                             [subscene/get-release-page-result
                             opensubtitles/get-release-page-result
                             addicted/get-release-page-result
                             podnapisi/get-release-page-result
                             notabenoid/get-release-page-result
                             uksubtitles/get-release-page-result]))]
    (loop [i 0]
      (when-let [subtitle (<!! ch)]
        (models/create-document subtitle)
        (when (zero? (mod i 50))
          (println (str "Updated " i)))
        (recur (inc i))))))
