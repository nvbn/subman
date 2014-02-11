(ns subman.sources.podnapisi
  (:require [net.cgrand.enlive-html :as html]
            [subman.helpers :as helpers]
            [subman.const :as const]))

(defn- make-url [url] (str "http://www.podnapisi.net" url))

(defn- get-langs
  "Get langs with links"
  [] (->> (make-url "/en/ppodnapisi/")
          helpers/fetch
          (#(html/select % [:div#content :table :td.lang :a]))
          (drop 3)
          (map #(hash-map :lang (-> (:content %)
                                    first
                                    clojure.string/trim)
                          :lang-url (-> (:attrs %)
                                        :href
                                        make-url)))))

(defn- get-lang-letters
  "Get lang letters urls"
  [lang] (map #(str (:lang-url lang) "/sS/movie/sO/desc/crka/" %)
              (list* "*" (map #(-> % char str)
                              (range 65 91)))))

(defn- get-pages-count
  "Get pages count for letter"
  [url] (-> (helpers/fetch url)
            (html/select [:div#content_left
                          :div.buttons
                          :div.left
                          :button.selector])
            first
            :attrs
            :pages
            read-string))

(defn- get-pages-urls
  "Get pages urls for letter"
  [url] (->> (get-pages-count url)
             inc
             (range 1)
             (map #(str url "/page/" %))))

(defn- download-element
  "Get download element"
  [item] (-> item
             (html/select [[:td html/first-child] :a.subtitle_page_link])
             first))

(defn- season-episode-part
  "Get item from season episode part"
  [item pos] (helpers/nil-to-blank (some-> item
                                           (html/select [[:td html/first-child]
                                                         :div.list_div2 :b])
                                           vec
                                           (get pos)
                                           :content
                                           first
                                           clojure.string/trim)))

(defn- create-subtitle-map
  "Create subtitle map from list page item"
  [item] {:show (-> (download-element item)
                    :content
                    first
                    clojure.string/trim)
          :url (-> (download-element item)
                   :attrs
                   :href
                   make-url)
          :season (season-episode-part item 1)
          :episode (season-episode-part item 2)
          :version (-> item
                       (html/select [[:td html/first-child] :span.release])
                       first
                       :content
                       last)
          :name ""
          :lang (-> item
                    (html/select [[:td (html/nth-child 3)]
                                  :div.flag])
                    first
                    :attrs
                    :alt
                    (clojure.string/split #" ")
                    first)})

(defn- parse-list-page
  "Parse page with subtitles list"
  [url] (-> (helpers/fetch url)
            (html/select [:div#content_left
                          :table.list
                          [:tr (html/has [:td])]])
            ((fn [page] (map #(create-subtitle-map %)
                            page)))))
(defn get-all-flat
  "Get all subtitles as flat list"
  [] (->> (pmap (fn [lang]
                 (->> (get-lang-letters lang)
                      (pmap (helpers/make-safe get-pages-urls nil))
                      (remove nil?)
                      flatten
                      (pmap (helpers/make-safe parse-list-page nil))
                      (remove nil?)
                      flatten
                      (map #(assoc % :lang (:lang lang)
                          :source const/type-podnapisi))))
               (get-langs))
          flatten))

(defn- get-release-page-url
  "Get release page url"
  [page] (-> "/en/ppodnapisi/search/sJ/-1/sS/time/sO/desc/sT/-1/sM/0/sA/0/sK//sOA/0/sOT/0/sOL/0/sOI/0/sOE/0/sOD/0/sOH/0/sY//sOCS/0/sFT/0/sR//sTS//sTE//sAKA/1/sH//sI//tbsl/1/asdp/0/page//page/"
             (str page)
             make-url))

(defn get-release-page-result
  "Get release page result"
  [page] (map #(assoc % :source const/type-podnapisi)
              (-> (get-release-page-url page)
                  parse-list-page
                  flatten)))

