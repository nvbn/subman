(ns subman.sources.t-addicted
  (:use midje.sweet)
  (:require [subman.sources.addicted :as addicted]
            [net.cgrand.enlive-html :as html]))

(defn- get-from-file
  "Get parsed html from file"
  [path] (html/html-resource (java.io.StringReader.
                              (slurp path))))

(defn get-shows
  "Get parsed html for all shows"
  [] (get-from-file "test/subman/sources/fixtures/addicted_shows.html"))

(defn get-single-show
  "Get parsed html for single show"
  [] (get-from-file "test/subman/sources/fixtures/addicted_show.html"))

(defn get-single-episode
  "Get parsed html for single episode"
  [] (get-from-file "test/subman/sources/fixtures/addicted_episode.html"))

(facts "shows list parser"
       (fact "return all shows"
             (count (addicted/get-shows)) => 2741
             (provided (addicted/fetch anything) => (get-shows)))
       (fact "return correct show maps"
             (-> (addicted/get-shows)
                 first
                 (#(and (contains? % :name)
                        (contains? % :url)))) => true
             (provided (addicted/fetch anything) => (get-shows))))

(facts "single show parser"
       (fact "return all seasons"
             (count (addicted/get-episodes {:url ""})) => 9
             (provided (addicted/fetch anything) => (get-single-show)))
       (fact "return all episodes"
             (-> (addicted/get-episodes {:url ""})
                 first
                 :episodes
                 count) => 10
             (provided (addicted/fetch anything) => (get-single-show))))

(facts "single episode parser"
       (fact "return all versions"
             (count (addicted/get-versions {:url ""})) => 2
             (provided (addicted/fetch anything) => (get-single-episode)))
       (fact "return all languages"
             (-> (addicted/get-versions {:url ""})
                 first
                 :langs
                 count) => 3
             (provided (addicted/fetch anything) => (get-single-episode))))
