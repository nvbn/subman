(ns subman.sources.opensubtitles
  (:require [swiss.arrows :refer [-<>]]
            [net.cgrand.enlive-html :as html]
            [subman.helpers :as helpers]
            [subman.const :as const]))

(defn- make-url
  "Make absolute url from relative"
  [url]
  (str "http://www.opensubtitles.org" url))

(defn- get-page-url
  "Get new subtitles page url"
  [page]
  (make-url (str "/en/search/sublanguageid-all/offset-"
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

(defn- get-seasons-part
  "Get season part from td"
  [titles-td]
  (some-> (:content titles-td)
          vec
          (get 2)))

(defn- get-show-part
  "Get show part from main link"
  [main-link]
  (-> main-link
      :content
      first
      remove-brs))

(defn- get-url
  "Get url from main link"
  [main-link]
  (-> main-link
      :attrs
      :href
      make-url))

(defn- get-version
  "Get version from titles"
  [titles-td]
  (helpers/nil-to-blank (some-> titles-td
                                (html/select [:span])
                                first
                                :content
                                first)))

(defn- get-language
  "Get language from td"
  [lang-td]
  (-> (html/select lang-td [:a])
      first
      :attrs
      :title))

(defn- create-subtitle
  "Create subtitle map from tr"
  [line]
  (let [tds (html/select line [:td])
        titles-td (first tds)
        main-link (first (html/select titles-td [:strong :a]))
        seasons-part (get-seasons-part titles-td)
        show-part (get-show-part main-link)
        lang-td (nth tds 1)]
    {:show (get-from-show-part #"\"(.+)\"" show-part show-part)
     :name (get-from-show-part #"\".+\" (.+)" show-part)
     :url (get-url main-link)
     :version (get-version titles-td)
     :season (get-from-season-part #"\[S(\d+)" seasons-part)
     :episode (get-from-season-part #"E(\d+)\]" seasons-part)
     :lang (get-language lang-td)
     :source const/type-opensubtitles}))

(defn get-release-page-result
  "Get release page result"
  [page]
  (-<> (get-page-url page)
       helpers/fetch
       (html/select [:table#search_results
                     [:tr.expandable (html/has [:strong])]])
       (map create-subtitle <>)))
