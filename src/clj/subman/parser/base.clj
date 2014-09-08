(ns subman.parser.base)

(defprotocol IndexSource
  (source-name [this])
  (download-enabled? [this])
  (get-htmls-for-parse [this page])
  (get-subtitles [this html])
  (get-type [this]))

(defmacro defsource
  "Define source"
  [name & {:keys [download-enabled? get-htmls-for-parse get-subtitles type-id]
           :or {download-enabled? true}}]
  `(def ~name
     (reify IndexSource
       (source-name [_] ~(str name))
       (download-enabled? [_] ~download-enabled?)
       (get-type [_] ~type-id)
       (get-htmls-for-parse [_ page#] (remove nil? (~get-htmls-for-parse page#)))
       (get-subtitles [this# html#] (->> (~get-subtitles html#)
                                         (remove nil?)
                                         (map #(assoc % :source (.get-type this#))))))))
