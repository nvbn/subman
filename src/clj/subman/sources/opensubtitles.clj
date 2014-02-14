(ns subman.sources.opensubtitles
  (:require [net.cgrand.enlive-html :as html]
            [subman.helpers :as helpers]
            [subman.const :as const]))

(def force-lang "English")

(defn- make-url
  "Make absolute url from relative"
  [url] (str "http://www.opensubtitles.org" url))

(defn- get-page-url
  "Get new subtitles page url"
  [page] (make-url (str "/en/search/sublanguageid-eng/offset-"
                        (* 40 (dec page)))))

(defn- get-from-season-part
  "Get from season part"
  [re part] (if (or (nil? part)
                    (not (string? part)))
              ""
              (if-let [result (some-> (re-find re part)
                                      last
                                      helpers/remove-first-0)]
                      result
                      "")))

(defn- create-subtitle
  "Create subtitle map from tr"
  [line] (let [tds (html/select line [:td])
               titles-td (first tds)
               main-link (-> titles-td
                             (html/select [:strong :a])
                             first)
               seasons-part (some-> titles-td
                                    :content
                                    vec
                                    (get 2))]
           {:show (-> main-link
                      :content
                      first)
            :url (-> main-link
                     :attrs
                     :href
                     make-url)
            :version (or (some-> titles-td
                                 (html/select [:span])
                                 first
                                 :content
                                 first) "")
            :season (get-from-season-part #"\[S(\d+)" seasons-part)
            :episode (get-from-season-part #"E(\d+)\]" seasons-part)
            :lang force-lang
            :source const/type-opensubtitles}))

(defn get-release-page-result
  "Get release page result"
  [page] (-> (get-page-url page)
             helpers/fetch
             (html/select [:table#search_results
                           [:tr.expandable (html/has [:strong])]])
             (#(map create-subtitle %))))

