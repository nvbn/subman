(ns subman.parser.sources.podnapisi
  (:require [swiss.arrows :refer [-<> some-<>]]
            [net.cgrand.enlive-html :as html]
            [subman.helpers :as helpers :refer [defsafe]]
            [subman.const :as const]
            [subman.parser.base :refer [defsource]]))

(defn- make-url [url] (str "http://www.podnapisi.net" url))

(defn- season-episode-part
  "Get item from season episode part"
  [item pos]
  (helpers/nil-to-blank (some-> item
                                (html/select [[:td html/first-child]
                                              :div.list_div2 :b])
                                vec
                                (get pos)
                                :content
                                first
                                clojure.string/trim
                                helpers/remove-first-0)))

(defn- get-show
  "Get show from download item"
  [download-element]
  (-> (:content download-element)
      first
      clojure.string/trim))

(defn- get-url
  "Get url from download item"
  [download-element]
  (-> download-element
      :attrs
      :href
      make-url))

(defn- get-version
  "Get version from line"
  [item]
  (some-> (html/select item [[:td html/first-child] :span.release])
          first
          :content
          last
          helpers/remove-spec-symbols))

(defn- get-lang
  "Get lang from line"
  [item]
  (-> (html/select item [[:td (html/nth-child 3)]
                         :div.flag])
      first
      :attrs
      :alt
      (clojure.string/split #" ")
      first))

(defn- create-subtitle-map
  "Create subtitle map from list page item"
  [item]
  (let [download-element (first
                           (html/select item [[:td html/first-child]
                                              :a.subtitle_page_link]))]
    {:show (get-show download-element)
     :url (get-url download-element)
     :season (season-episode-part item 1)
     :episode (season-episode-part item 2)
     :version (get-version item)
     :name ""
     :lang (get-lang item)}))

(defsafe parse-list-page
  "Parse page with subtitles list"
  [html]
  (-<> (helpers/get-from-line html)
       (html/select [:div#content_left
                     :table.list
                     [:tr (html/has [:td])]])
       (map create-subtitle-map <>)))

(defn- get-release-page-url
  "Get release page url"
  [page]
  (-> "/en/ppodnapisi/search/sJ/-1/sS/time/sO/desc/sT/-1/sM/0/sA/0/sK//sOA/0/sOT/0/sOL/0/sOI/0/sOE/0/sOD/0/sOH/0/sY//sOCS/0/sFT/0/sR//sTS//sTE//sAKA/1/sH//sI//tbsl/1/asdp/0/page//page/"
      (str page)
      make-url))

(defsafe get-htmls-for-parse
  "Get htmls for parse subtitles"
  [page]
  [(helpers/download (get-release-page-url page))])

(defsafe get-subtitles
  "Get subtitles from html"
  [html]
  (flatten (parse-list-page html)))

(defsource podnapisi-source
  :type-id const/type-podnapisi
  :get-htmls-for-parse get-htmls-for-parse
  :get-subtitles get-subtitles)
