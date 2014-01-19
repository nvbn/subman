(ns subman.filler
  (:require [subman.sources.addicted :as addicted]
            [subman.models :as models]))

(defn- create-show-season-map
  "Create show-season mapping"
  [show getter] (map #(hash-map :show (:name show)
                                :season (:number %)
                                :episodes (:episodes %))
              (getter show)))

(defn- create-show-episode-map
  "Create show-episode mapping"
  [show] (map #(-> show
                   (dissoc :episodes)
                   (assoc :episode (:number %)
                     :name (:name %)
                     :url (:url %)))
              (:episodes show)))

(defn- create-episode-version-map
  "Create episode-version mapping"
  [episode getter] (map #(-> episode
                             (dissoc :url)
                             (assoc :langs (:langs %)
                               :version (:name %)))
                        (getter episode)))

(defn- create-episode-lang-map
  "Create episode-lang mapping"
  [episode] (map #(-> episode
                      (dissoc :langs)
                      (assoc :lang (:name %)
                        :url (:url %)))
                 (:langs episode)))

(defn get-flattened
  "Get flattened subs maps"
  [get-shows get-episodes get-versions]
  (->> (get-shows)
       (pmap #(create-show-season-map % get-episodes))
       flatten
       (pmap create-show-episode-map)
       flatten
       (pmap #(create-episode-version-map % get-versions))
       flatten
       (pmap create-episode-lang-map)
       flatten))

(defn load-all
  "Load all subtitles"
  [] (models/delete-all)
  (->> (get-flattened addicted/get-shows
                      addicted/get-episodes
                      addicted/get-versions)
       (map models/create-document)
       (map-indexed vector)
       (map (fn [[i item]]
              (when (= (mod i 50) 0)
                (println (str "Loaded " i)))
              item))
       doall))
