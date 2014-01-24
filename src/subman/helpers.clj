(ns subman.helpers
  (:require [net.cgrand.enlive-html :as html]))

(defn print-first [arg fnc & args]
  (println arg)
  (apply fnc arg args))

(defn remove-first-0
  "Remove first 0 from string"
  [query] (clojure.string/replace query #"^(0+)" ""))

(defn fetch
  "Fetch url content"
  [url] (-> url
            java.net.URL.
            html/html-resource))
