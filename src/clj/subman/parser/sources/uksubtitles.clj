(ns subman.parser.sources.uksubtitles
  (:require [clojure.string :as string]
            [swiss.arrows :refer [-<>>]]
            [net.cgrand.enlive-html :as html]
            [subman.helpers :as helpers :refer [defsafe]]
            [subman.const :as const]
            [subman.parser.base :refer [defsource]]))

(def force-lang "english")

(defn- get-release-page-url
  "Get url of release page"
  [page]
  (str "http://uksubtitles.ru/"
       (if (> page 1)
         (str "page/" page "/")
         "")))

(defn- get-articles
  "Get articles from page html"
  [page-html]
  (html/select page-html [:article]))

(defn- parse-article
  "Parse data from single article"
  [article]
  (let [title-a (first (html/select article
                                    [:header :h2 :a]))]
    {:title (first (:content title-a))
     :url (:href (:attrs title-a))
     :subtitles (map #(last (:content %))
                     (html/select article
                                  [:div (html/has [:a.wpfb-dlbtn])]))}))

(defn- get-name-from-download
  "Get name from download line"
  [line]
  (let [prepared (string/replace line #"\." " ")]
    (second (or (re-find #"\n*(.*) [sS](\d*?)[eE]" prepared)
                (re-find #"\n*(.*) (720p|1080p)" prepared)
                (re-find #"\n*(.*) srt" prepared)
                [nil prepared]))))

(defn get-version
  [line]
  (helpers/remove-spec-symbols (string/replace line
                                               #" \(.*Download.*\).*" "")))

(defn- get-subtitle-data-from-download
  "Get subtitle data from donwload line"
  [line]
  (let [[season episode] (helpers/get-season-episode line)]
    {:season season
     :episode episode
     :show (get-name-from-download line)
     :version (get-version line)}))

(defn- get-subtitles-from-article
  "Get subtitles from article map"
  [article]
  (let [subtitles (if (seq (:subtitles article))
                    (map get-subtitle-data-from-download (:subtitles article))
                    [{:show (string/replace (:title article) #"Subtitles for " "")}])]
    (map #(assoc %
           :url (:url article)
           :lang force-lang
           :name "")
         subtitles)))

(defsafe get-htmls-for-parse
  [page]
  [(helpers/download (get-release-page-url page))])

(defsafe get-subtitles
  [html]
  (-<>> (helpers/get-from-line html)
        get-articles
        (map parse-article)
        (map get-subtitles-from-article)
        flatten))

(defsource uksubtitles-source
  :type-id const/type-uksubtitles
  :get-htmls-for-parse get-htmls-for-parse
  :get-subtitles get-subtitles)
