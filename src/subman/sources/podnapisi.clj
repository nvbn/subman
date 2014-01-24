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
             (html/select [:a.subtitle_page_link])
             first))

(defn- season-episode-part
  "Get item from season episode part"
  [item pos] (helpers/nil-to-blank (some-> item
                                           (html/select [:div.list_div2 :b])
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
          :page-url (-> (download-element item)
                        :attrs
                        :href
                        make-url)
          :season (season-episode-part item 1)
          :episode (season-episode-part item 2)
          :version (-> item
                       (html/select [:span.release])
                       first
                       :content
                       last)
          :name ""})

(defn- parse-list-page
  "Parse page with subtitles list"
  [url] (-> (helpers/fetch url)
            (html/select [:div#content_left
                          :table.list
                          :td.sort_column])
            ((fn [page] (map #(create-subtitle-map %)
                            page)))))

(defn- get-download-url
  "Get subtitle download url"
  [page-url] (-> (helpers/fetch page-url)
                 (html/select [:a.download])
                 first
                 :attrs
                 :href
                 make-url))

(defn get-all-flat
  "Get all subtitles as flat list"
  [] (->> (pmap (fn [lang]
                 (->> (get-lang-letters lang)
                      (pmap get-pages-urls)
                      flatten
                      (pmap parse-list-page)
                      flatten
                      (pmap #(assoc % :lang (:lang lang)
                          :url (get-download-url (:page-url %))
                          :source const/type-podnapisi))
                      (pmap #(dissoc % :page-url))))
               (get-langs))
          flatten))
