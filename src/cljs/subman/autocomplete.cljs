(ns subman.autocomplete
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.reader :refer [read-string]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [jayq.core :refer [$]]
            [subman.const :as const]))

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

(defn completion-source
  "Source for typeahead autocompletion"
  [langs sources query cb]
  (cb (->> (get-completion query
                           @langs
                           @sources)
           (map #(js-obj "value" %))
           (take const/autocomplete-limit)
           (apply array))))

(defn init-autocomplete
  "Initiale autocomplete"
  [query langs sources]
  (let [input ($ "#search-input")]
    (.typeahead input
                #js {:highlight true}
                #js {:source (partial completion-source
                                      langs sources)})
    (.on input "typeahead:closed"
         #(reset! query (.val input)))))
