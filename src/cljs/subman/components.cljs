(ns subman.components
  (:require [subman.const :as const]
            [subman.helpers :refer [is-filled?]]))

(defn search-box
  "Search box"
  [value]
  [:div.input-group.input-group-lg.col-xs-12.search-input
   {:data-spy "affix"
    :data-offset-top "40"}
   [:span.input-group-addon (if (= "" @value)
                              [:i.fa.fa-search]
                              [:a {:on-click (fn [e]
                                               (.preventDefault e)
                                               (reset! value ""))
                                   :href "#"}
                               [:i.fa.fa-chevron-left]])]
   [:input.form-control {:type "text"
                         :placeholder "Type search query"
                         :on-change #(reset! value (-> % .-target .-value))
                         :value @value
                         :id "search-input"}]])

(defn result-line
  "Search result line"
  [{:keys [url name show season episode source lang version]}]
  [:a.list-group-item.search-result {:href url
                                     :target "_blank"}
   [:h3
    show (when (is-filled? name) (str " - " name))
    (when (some is-filled? [season episode])
      [:span " " (when (is-filled? season) (str "S" season))
       (when (is-filled? episode) (str "E" episode))])]
   [:p.pull-right "Source: " (const/type-names source)]
   [:p "Language: " lang]
   (when (is-filled? version)
     [:p "Version: " version])])

(defn info-box
  "Show info box"
  [text]
  [:div.container.col-xs-12.info-box
   [:h2 text]])

(defn result-list
  "Search result list"
  [query items counter total-count in-progress]
  (cond
   (> (count @items) 0) [:div.container.col-xs-12.search-result-holder
                         [:div.search-result-list.list-group (map result-line @items)]]
   (or (= @counter 0)
       (= (count @query) 0)) [:div.container.col-xs-12.info-box
                              [:h2 "Welcome to subman!"]
                              [:p "We indexing "
                               [:a {:href "http://www.addic7ed.com/"
                                    :target "_blank"} "addic7ed.com"]
                               ", "
                               [:a {:href "http://www.opensubtitles.org/"
                                    :target "_blank"} "www.opensubtitles.org"]
                               ", "
                               [:a {:href "http://www.podnapisi.net/"
                                    :target "_blank"} "podnapisi.net"]
                               " and "
                               [:a {:href "http://subscene.com/"
                                    :target "_blank"} "subscene.com"]
                               "."]
                              [:p "In query you can specifie language with "
                               [:code ":lang name"]
                               ", by default used english."]
                              [:p "Total indexed subtitles count: "
                               @total-count
                               "."]
                              [:a {:href "https://github.com/nvbn/subman"
                                   :target "_blank"}
                               [:i.fa.fa-github] " github"]]
   (true? @in-progress) [info-box "Searching..."]
   :else [info-box (str "Nothing found for \"" @query "\"")]))

(defn search-page
  "Search component"
  [query results counter total-count in-progress]
  [:div [search-box query]
   [result-list
    query
    results
    counter
    total-count
    in-progress]])
