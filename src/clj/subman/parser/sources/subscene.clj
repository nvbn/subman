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

(defn get-url
  "Get url from page"
  [page]
  (-> (html/select page [:div.download :a])
      first
      :attrs
      :href
      make-url))

(defsafe create-subtitle
  "Create subtitle from page url"
  [page]
  (let [version (get-version page)
        season-episode (helpers/get-season-episode version)]
    {:show (get-show page)
     :season (get season-episode 0)
     :episode (get season-episode 1)
     :version version
     :url (get-url page)
     :source const/type-subscene
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
        (map helpers/download)))

(defsafe get-subtitles
  [html]
  [(create-subtitle (helpers/get-from-line html))])

(defsource subscene-source
  :get-htmls-for-parse get-htmls-for-parse
  :get-subtitles get-subtitles)
