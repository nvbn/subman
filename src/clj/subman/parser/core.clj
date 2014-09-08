(ns subman.parser.core
  (:require [clojure.core.async :as async :refer [<!! >!]]
            [clojure.tools.logging :as log]
            [subman.parser.sources.addicted :refer [addicted-source]]
            [subman.parser.sources.podnapisi :refer [podnapisi-source]]
            [subman.parser.sources.opensubtitles :refer [opensubtitles-source]]
            [subman.parser.sources.subscene :refer [subscene-source]]
            [subman.parser.sources.notabenoid :refer [notabenoid-source]]
            [subman.parser.sources.uksubtitles :refer [uksubtitles-source]]
            [subman.models :as models]
            [subman.const :as const]
            [subman.helpers :as helpers]))

(def sources (atom [addicted-source
                    podnapisi-source
                    opensubtitles-source
                    subscene-source
                    notabenoid-source
                    uksubtitles-source]))

(defn- get-new-for-page
  "Get new subtitles for page"
  [source checker page]
  (for [html (.get-htmls-for-parse source page)
        subtitle (.get-subtitles source html)
        :when (checker subtitle)]
    subtitle))

(defn get-new-subtitles-in-chan
  "Get new result from pages in chan"
  [source checker]
  (let [result (async/chan)]
    (async/thread
      (async/go-loop [page 1]
                     (when (<= page const/update-deep)
                       (if-let [page-result (seq (get-new-for-page source
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
                             @sources))
        update-id (gensym)]
    (log/info (str "Start update " update-id))
    (loop [i 0]
      (if-let [subtitle (<!! ch)]
        (do (models/create-document! subtitle)
            (when (zero? (mod i 50))
              (log/info (str "Update " update-id " progress: " i)))
            (recur (inc i)))
        (log/info (str "Update " update-id " finished: " i))))))
