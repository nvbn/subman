(ns subman.sources.addicted
  (:require [net.cgrand.enlive-html :as html]
            [subman.helpers :as helpers]
            [subman.const :as const]))

(defn- make-url
  "Make url for addicted"
  [end-part] (str "http://www.addic7ed.com"
                  (if (nil? (re-find #"^/" end-part))
                    (str "/" end-part)
                    end-part)))

(defn- is-version-line?
  "Is it line with version?"
  [line] (some-> line
                 (html/select [:td.NewsTitle :img])
                 first
                 :attrs
                 :src
                 (= "/images/folder_page.png")))

(defn- is-language-line?
  "Is it language line?"
  [line] (some-> line
                 (html/select [:td.language :a])
                 vec
                 count
                 (> 0)))

(defn- get-version
  "Get version map from line"
  [line] {:name (-> line
                    (html/select [:td.NewsTitle :b])
                    first
                    :content
                    vec
                    (get 1 ""))
          :langs []})

(defn- get-lang
  "Get lang map from line"
  [line] {:name (-> line
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
  [line version] (let [langs (:langs version)]
                   (assoc version
                     :langs (list* (get-lang line) langs))))

(defn- get-subtitles
  "Get subtitles from lines"
  [lines] (reduce (fn [buffer line]
                    (cond
                     (is-version-line? line) (list* (get-version line)
                                                    buffer)
                     (is-language-line? line) (list* (add-lang line (first buffer))
                                                     (rest buffer))
                     :else buffer))
                  [] lines))

(defn get-versions
  "Get versions of subtitles for single episode"
  [episode] (-> episode
                :url
                helpers/fetch
                (html/select [:table.tabel95 :table.tabel95 :tr])
                get-subtitles))

(defn- get-releases-url
  "Get releases url for page"
  [page] (str "http://www.addic7ed.com/log.php?mode=versions&page=" page))

(defn- episode-from-release
  "Episode from release page item"
  [item] (let [name-parts (-> (:content item)
                              first
                              (clojure.string/split #" - "))
               season-episode (-> name-parts
                                  (get 1)
                                  (clojure.string/split #"x"))]
           {:show (first name-parts)
            :season (-> season-episode first helpers/remove-first-0)
            :episode (-> season-episode last helpers/remove-first-0)
            :name (last name-parts)
            :url (-> item :attrs :href make-url)}))

(defn get-release-page-result
  "Get release page result"
  [page] (-> (get-releases-url page)
             helpers/fetch
             (html/select [:table.tabel :tr])
             (#(drop 2 %))
             (html/select [(html/nth-child 2) :a])
             flatten
             (#(map (helpers/make-safe episode-from-release nil) %))
             (#(remove nil? %))
             (#(map (fn [episode] (for [version (get-versions episode)
                                        lang (:langs version)]
                                    (assoc episode :version (:name version)
                                      :lang (:name lang)
                                      :url (:url lang)
                                      :source const/type-addicted)))
                    %))
             flatten))
