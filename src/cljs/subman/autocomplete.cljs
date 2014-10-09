(ns subman.autocomplete
  (:require [clojure.string :as string]))

(defn only-contains
  "Get only contains from list"
  [items needle]
  (filter #(re-find (re-pattern (string/lower-case needle))
                    (string/lower-case %)) items))

(defn with-value
  "List of items with value"
  [splited items]
  (map #(string/join " " (conj splited %))
       items))

(defn with-value-contains
  [reversed items needle]
  (only-contains (map #(string/join " "
                                    (-> (rest reversed)
                                        reverse
                                        vec
                                        (conj %)))
                      items)
                 needle))

(defn get-completion
  "Get autocomplete values list"
  [query langs sources]
  (let [splited (string/split query #" ")
        reversed (reverse splited)
        last-token (some-> reversed first string/lower-case)
        pre-last-token (some-> reversed second string/lower-case)]
    (cond
      (= last-token ":lang") (with-value splited langs)
      (= pre-last-token ":lang") (with-value-contains reversed langs last-token)
      (= last-token ":source") (with-value splited sources)
      (= pre-last-token ":source") (with-value-contains reversed sources last-token)
      (re-find #"^:" last-token) (with-value (-> splited butlast vec)
                                             [":lang" ":source"])
      :else [])))
