(ns subman.parser.base)

(defprotocol IndexSource
  (source-name [this])
  (download-enabled? [this])
  (get-htmls-for-parse [this page])
  (get-subtitles [this html url])
  (get-type [this])
  (make-url [this url]))

(defmacro defsource
  "Define source"
  [name & {:keys [download-enabled? get-htmls-for-parse get-subtitles
                  type-id make-url]
           :or {download-enabled? true}}]
  `(def ~name
     (reify IndexSource
       (source-name [_] ~(str name))
       (download-enabled? [_] ~download-enabled?)
       (get-type [_] ~type-id)
       (get-htmls-for-parse [_ page#] (remove nil? (~get-htmls-for-parse page#)))
       (get-subtitles [this# html# url#] (->> (~get-subtitles html# url#)
                                         (remove nil?)
                                         (map #(assoc % :source (.get-type this#)))))
       (make-url [_ url#] (~make-url url#)))))
