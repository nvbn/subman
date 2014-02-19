(ns subman.sources.opensubtitles
  (:require [swiss.arrows :refer [-<>]]
            [net.cgrand.enlive-html :as html]
            [subman.helpers :as helpers]
            [subman.const :as const]))

(def force-lang "English")

(defn- make-url
  "Make absolute url from relative"
  [url]
  (str "http://www.opensubtitles.org" url))

(defn- get-page-url
  "Get new subtitles page url"
  [page]
  (make-url (str "/en/search/sublanguageid-eng/offset-"
                 (* 40 (dec page)))))

(defmacro get-from-part
  "Get from part using getter"
  [re part default & getter]
  `(if (or (nil? ~part)
           (not (string? ~part)))
     ~default
     (let [result# (some-> (re-find ~re ~part)
                           last
                           ~@getter)]
       (if (or (nil? result#)
               (= "" result#))
         ~default
         result#))))

(defn- get-from-season-part
  "Get from season part"
  [re part]
  (get-from-part re part "" helpers/remove-first-0))

(defn- remove-brs
  "Remove <br> from item"
  [item]
  (clojure.string/replace item #"<br */*>" " "))

(defn- get-from-show-part
  "Get matched form show part"
  ([re part] (get-from-show-part re part ""))
  ([re part default] (get-from-part re part default)))

(defn- create-subtitle
  "Create subtitle map from tr"
  [line]
  (let [tds (html/select line [:td])
        titles-td (first tds)
        main-link (-> titles-td
                      (html/select [:strong :a])
                      first)
        seasons-part (some-> titles-td
                             :content
                             vec
                             (get 2))
        show-part (-> main-link
                      :content
                      first
                      remove-brs)]
    {:show (get-from-show-part #"\"(.+)\"" show-part show-part)
     :name (get-from-show-part #"\".+\" (.+)" show-part)
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
  [page]
  (-<> (get-page-url page)
       helpers/fetch
       (html/select [:table#search_results
                     [:tr.expandable (html/has [:strong])]])
       (map create-subtitle <>)))
