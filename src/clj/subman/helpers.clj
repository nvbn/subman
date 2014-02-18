(ns subman.helpers
  (:require [net.cgrand.enlive-html :as html]))

(defn print-first [arg fnc & args]
  (println arg)
  (apply fnc arg args))

(defn print-identity [item]
  (println item)
  item)

(defn remove-first-0
  "Remove first 0 from string"
  [query] (clojure.string/replace query #"^(0+)" ""))

(defn fetch
  "Fetch url content"
  [url] (-> url
            java.net.URL.
            html/html-resource))

(defn nil-to-blank
  "Replace nil with blank string"
  [item] (if (nil? item)
           ""
           item))

(defn make-safe
  "Make fnc call safe"
  [fnc fallback] (fn [x]
                   (try (fnc x)
                     (catch Exception e (do (println e)
                                          fallback)))))

(defn get-season-episode
  "Add season and episode filters"
  [text] (if-let [nums (re-find #"[sS](\d+)[eE](\d+)" text)]
           [(remove-first-0 (get nums 1))
            (remove-first-0 (get nums 2))]
           ["" ""]))

(defn get-from-file
  "Get parsed html from file"
  [path] (html/html-resource (java.io.StringReader.
                              (slurp path))))

(defn get-from-line
  "Get parsed html from line"
  [line] (html/html-resource (java.io.StringReader. line)))
