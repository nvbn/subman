(ns subman.core
  (:require [overtone.at-at :as at-at]
            [subman.handlers :as h]
            [subman.parser.core :as parser]
            [subman.const :as const]
            [subman.models :as models]))

(defn init-pool
  "Init pull for running periodic tasks"
  []
  (let [pool (at-at/mk-pool)]
    (at-at/every const/update-period
                 #(future (parser/load-new-subtitles))
                 pool)
    (at-at/every const/sitemap-period
                 #(future (models/update-unique-show-season-episode!))
                 pool)
    (at-at/every const/crawl-period
                 #(future (parser/load-all))
                 pool)))

(defn -main
  [& _]
  (println "Start parsers")
  (parser/inject!)
  (h/init-models)
  (init-pool))
