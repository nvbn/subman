(ns subman.sources.utils)

(defprotocol IndexSource
  (source-name [this])
  (download-enabled? [this])
  (get-htmls-for-parse [this page])
  (get-subtitles [this html]))

(defmacro defsource
  "Define source"
  [name & {:keys [download-enabled? get-htmls-for-parse get-subtitles]
           :or [download-enabled? true]}]
  `(def ~name (reify IndexSource
                (source-name [_] ~(str name))
                (download-enabled? [_] ~download-enabled?)
                (get-htmls-for-parse [_ page#] (~get-htmls-for-parse page#))
                (get-subtitles [_ html#] (~get-subtitles html#)))))
