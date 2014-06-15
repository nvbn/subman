(ns subman.helpers
  (:require [clojure.stacktrace :refer [print-cause-trace]]
            [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]
            [subman.const :as const]))

(defn remove-first-0
  "Remove first 0 from string"
  [query]
  (clojure.string/replace query #"^(0+)" ""))

(defn get-from-line
  "Get parsed html from line"
  [line]
  (html/html-resource (java.io.StringReader. line)))

(defn fetch
  "Fetch url content"
  [url]
  (-> url
      (http/get {:timeout const/conection-timeout})
      deref
      :body
      get-from-line))

(defn nil-to-blank
  "Replace nil with blank string"
  [item]
  (if (nil? item)
    ""
    item))

(defn make-safe
  "Make fnc call safe"
  [fnc fallback]
  (fn [x]
    (try (fnc x)
      (catch Exception e (do
                           (println (str "FAIL START:\n" fnc " WITH " x))
                           (print-cause-trace e)
                           (println (str "FAIL END:\n" fnc " WITH " x))
                           fallback)))))

(defn get-season-episode
  "Add season and episode filters"
  [text]
  (if-let [[_ season episode] (re-find #"[sS](\d+)[eE](\d+)" text)]
    [(remove-first-0 season)
     (remove-first-0 episode)]
    ["" ""]))

(defn get-from-file
  "Get parsed html from file"
  [path]
  (html/html-resource (java.io.StringReader.
                       (slurp path))))

(defn make-static
  "Make paths static"
  [& paths]
  (map #(str const/static-path %) paths))

(defn as-static
  "Call as static"
  [callable & paths]
  (apply callable (apply make-static paths)))
