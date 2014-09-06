(ns subman.sources.addicted
  (:require [clojure.string :as string]
            [net.cgrand.enlive-html :as html]
            [swiss.arrows :refer [-<>> -<>]]
            [subman.helpers :as helpers :refer [defsafe]]
            [subman.const :as const]
            [subman.sources.utils :refer [defsource]]))

(defn- make-url
  "Make url for addicted"
  [end-part]
  (str "http://www.addic7ed.com"
       (if (nil? (re-find #"^/" end-part))
         (str "/" end-part)
         end-part)))

(defn- is-version-line?
  "Is it line with version?"
  [line]
  (some-> line
          (html/select [:td.NewsTitle :img])
          first
          :attrs
          :src
          (= "/images/folder_page.png")))

(defn- is-language-line?
  "Is it language line?"
  [line]
  (some-> line
          (html/select [:td.language :a])
          vec
          count
          (> 0)))

(defn- get-version
  "Get version map from line"
  [line]
  {:name (-> line
             (html/select [:td.NewsTitle :b])
             first
             :content
             vec
             (get 1 ""))
   :langs []})

(defn- get-lang
  "Get lang map from line"
  [line]
  {:name (-> line
             (html/select [:td.language])
             first
             :content
             first
             :content
             first)
   :url (-> line
            (html/select [:a.buttonDownload])
            first
            :attrs
            :href
            make-url)})

(defn- add-lang
  "Add lang to version map"
  [line version]
  (let [langs (:langs version)]
    (assoc version
      :langs (list* (get-lang line) langs))))

(defn get-version-langs
  "Get subtitles from lines"
  [lines]
  (reduce (fn [buffer line]
            (cond
              (is-version-line? line) (list* (get-version line)
                                             buffer)
              (is-language-line? line) (list* (add-lang line (first buffer))
                                              (rest buffer))
              :else buffer))
          [] lines))

(defsafe get-versions
  "Get versions of subtitles for single episode"
  [episode-page]
  (get-version-langs (html/select episode-page
                                  [:table.tabel95 :table.tabel95 :tr])))

(defn- get-releases-url
  "Get releases url for page"
  [page]
  (str "http://www.addic7ed.com/log.php?mode=versions&page=" page))

(defn get-urls-from-list
  "Get urls from list page"
  [list-page]
  (-<> (html/select list-page [:table.tabel :tr])
       (drop 2 <>)
       (html/select [(html/nth-child 2) :a])
       (map #(-> % :attrs :href make-url) <>)))

(defsafe get-htmls-for-parse
  "Get list of htmls of subtitle pages"
  [page]
  (-<>> (get-releases-url page)
        helpers/fetch
        get-urls-from-list
        (map helpers/download)))

(defn get-episode-name-string
  "Get string with episode name"
  [episode-page]
  (-> (html/select episode-page [:.titulo])
      first
      :content
      first
      string/trim))

(defn get-episode-information
  "Get base episode information"
  [episode-page]
  (let [name-string (get-episode-name-string episode-page)
        name-parts (string/split name-string #" - ")
        [season episode] (helpers/get-season-episode name-string)]
    {:show (first name-parts)
     :season season
     :episode episode
     :name (last name-parts)}))

(defsafe get-subtitles
  "Get subtitles entries from html"
  [html]
  (let [page (helpers/get-from-line html)
        info (get-episode-information page)]
    (for [version (get-versions page)
          lang (:langs version)]
      (assoc info :version (:name version)
                  :lang (:name lang)
                  :url (:url lang)
                  :source const/type-addicted))))

(defsource addicted-source
  :get-htmls-for-parse get-htmls-for-parse
  :get-subtitles get-subtitles)
