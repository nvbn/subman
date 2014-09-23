(ns subman.parser.sources.subscene
  (:require [swiss.arrows :refer [-<>>]]
            [net.cgrand.enlive-html :as html]
            [subman.helpers :as helpers :refer [defsafe]]
            [subman.const :as const]
            [subman.parser.base :refer [defsource]]))

(defn- make-url
  "Make absolute url from relative"
  [url]
  (str "http://subscene.com" url))

(defn- get-page-url
  "Get release page url"
  [page]
  (make-url (str "/browse/latest/series/" page)))


(defn- remove-spec-symbols
  "Remove spec symbols"
  [text]
  (clojure.string/replace text #"[\t\n]" ""))

(defn- get-version
  "Get version from page"
  [page]
  (->> (html/select page [:li.release :div])
       (map #(-> %
                 :content
                 first
                 remove-spec-symbols))
       (clojure.string/join ", ")))

(defn- get-show
  "Get show from page"
  [page]
  (-> (html/select page [:h1 :span])
      first
      :content
      first
      remove-spec-symbols))

(defn- get-lang
  "Get lang from page"
  [page]
  (->> (html/select page [:div.download :a])
       first
       :content
       first
       (re-find #"Download (.*) Subtitle")
       last))

(defsafe create-subtitle
  "Create subtitle from page url"
  [page url]
  (let [version (get-version page)
        season-episode (helpers/get-season-episode version)]
    {:show (get-show page)
     :season (get season-episode 0)
     :episode (get season-episode 1)
     :version version
     :url url
     :lang (get-lang page)}))

(defsafe get-htmls-for-parse
  "Get htmls for parse for subtitles"
  [page]
  (-<>> (get-page-url page)
        helpers/fetch
        (html/select <> [:table :td.a1 :a])
        (map #(-> %
                  :attrs
                  :href
                  make-url))
        set
        (map helpers/download-with-url)))

(defsafe get-subtitles
  [html url]
  [(create-subtitle (helpers/get-from-line html) url)])

(defsource subscene-source
  :type-id const/type-subscene
  :get-htmls-for-parse get-htmls-for-parse
  :get-subtitles get-subtitles
  :make-url make-url)
