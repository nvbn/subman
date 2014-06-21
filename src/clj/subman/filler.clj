(ns subman.filler
  (:require [subman.sources.addicted :as addicted]
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

(defn- get-new-before-seq
  "Return lazy sequence with lists of results"
  ([getter checker] (get-new-before-seq getter checker 1))
  ([getter checker page]
   (if (> page const/update-deep)
     []
     (if-let [new-result (seq (get-new-for-page getter
                                                checker
                                                page))]
       (concat new-result
               (lazy-seq (get-new-before-seq getter checker
                                             (inc page))))
       []))))

(defn- get-new-before
  "Get new subtitles before checker"
  [getter checker]
  (flatten (get-new-before-seq getter checker)))

(defn- get-all-new
  "Get all new from callers with checker"
  [checker & callers]
  (mapcat #(get-new-before % checker) callers))

(defn update-all
  "Receive update from all sources"
  []
  (->> (get-all-new models/in-db
                    subscene/get-release-page-result
                    opensubtitles/get-release-page-result
                    addicted/get-release-page-result
                    podnapisi/get-release-page-result
                    notabenoid/get-release-page-result
                    uksubtitles/get-release-page-result)
       (map (helpers/make-safe models/create-document nil))
       (remove nil?)
       (map-indexed vector)
       (map (fn [[i item]]
              (when (zero? (mod i 50))
                (println (str "Updated " i)))
              item))
       doall))
