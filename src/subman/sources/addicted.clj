(ns subman.sources.addicted
  (:require [net.cgrand.enlive-html :as html]))

(defn- fetch
  "Fetch url content"
  [url] (-> url
            java.net.URL.
            html/html-resource))

(defn- make-url
  "Make url for addicted"
  [end-part] (str "http://www.addic7ed.com" end-part))

(defn- create-shows
  "Create show from html items"
  [items] (for [item items]
            {:name (-> item
                       :content
                       first)
             :url (-> item
                      :attrs
                      :href
                      make-url)}))

(defn get-shows
  "Get all available shows from addicted"
  [] (-> "/shows.php"
         make-url
         fetch
         (html/select [:td.version :h3 :a])
         create-shows))

(defn- get-episode-list-elements
  "Get episode list elements"
  [data] (html/select data [:div#header
                            [:a (html/has [(html/re-pred
                                            #"Episode list and air dates")])]]))

(defn- is-title?
  "Is line a title?"
  [line] (some-> line
                 :content
                 vec
                 (get 1)
                 :tag
                 (= :th)))

(defn- is-episode?
  "Is line a episode"
  [line] (some-> line
                 :content
                 first
                 :attrs
                 :align
                 (= "left")))

(defn- get-season-number
  "Get season number from line"
  [line] (-> line
             :content
             (nth 1)
             :content
             first
             :content
             first
             (clojure.string/split  #" ")
             last))

(defn- get-episode-name-element
  "Get episode name holder element"
  [line] (-> line
             :content
             (nth 6)
             :content
             first))

(defn- get-episode
  "Get episode from line"
  [line] {:number (-> line
                      :content
                      (nth 2)
                      :content
                      first
                      :content)
          :name (-> line
                    get-episode-name-element
                    :content
                    first)
          :url (-> line
                   get-episode-name-element
                   :attrs
                   :href)})

(defn- get-seasons
  "Get season with episodes"
  [lines] (reduce (fn [buffer line]
                    (cond
                     (is-title? line) (list* {:number (get-season-number line)
                                              :episodes []}
                                             buffer)
                     (is-episode? line) (list*
                                         (let [season (first buffer)
                                               episodes (:episodes season)]
                                           (assoc season
                                             :episodes (list* (get-episode line)
                                                              episodes)))
                                         (rest buffer))
                     :else buffer))
                  [] lines))

(defn get-episodes
  "Get episodes for show"
  [show] (-> show
             :url
             fetch
             get-episode-list-elements
             first
             :attrs
             :href
             make-url
             fetch
             (html/select [:table.tableEplist :tr])
             get-seasons))
