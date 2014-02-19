(ns subman.sources.subscene
  (:require [swiss.arrows :refer [-<>>]]
            [net.cgrand.enlive-html :as html]
            [subman.helpers :as helpers]
            [subman.const :as const]))

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

(defn- create-subtitle
  "Create subtitle from page url"
  [url]
  (let [page (helpers/fetch url)
        version (get-version page)
        season-episode (helpers/get-season-episode version)]
    {:show (get-show page)
     :season (get season-episode 0)
     :episode (get season-episode 1)
     :version version
     :url url
     :source const/type-subscene
     :lang (get-lang page)}))

(defn get-release-page-result
  "Get release page result"
  [page]
  (-<>> (get-page-url page)
        helpers/fetch
        (html/select <> [:table :td.a1 :a])
        (map #(-> %
                  :attrs
                  :href
                  make-url))
        set
        (map create-subtitle)))
