(ns subman.search
  (:use [cljs.reader :only [read-string]]
        [reagent.core :only [atom]]
        [subman.helpers :only [is-filled?]]
        [subman.history :only [init-history]])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [subman.const :as const]))

(defn search-box
  "Search box"
  [{:keys [value]}] [:div.input-group.input-group-lg.col-xs-12.search-inpu
                     {:data-spy "affix"
                      :data-offset-top "40"}
                     [:span.input-group-addon [:i.fa.fa-search]]
                     [:input.form-control {:type "text"
                                           :placeholder "Type search query"
                                           :on-change #(reset! value (-> % .-target .-value))
                                           :value @value}]])

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

(defn result-list
  "Search result list"
  [{:keys [query items counter]}]
  (cond
   (> (count @items) 0) [:div.container.col-xs-12
                         [:div.search-result-list.list-group (map result-line @items)]]
   (or (= @counter 0)
       (= (count @query) 0)) [:div.container.col-xs-12.info-box
                              [:h2 "Welcome to subman!"]
                              [:p "We indexing "
                               [:a {:href "http://www.addic7ed.com/"
                                    :target "_blank"} "addic7ed.com"]
                               " and "
                               [:a {:href "http://www.podnapisi.net/"
                                    :target "_blank"} "podnapisi.net"]
                               "."]
                              [:a {:href "https://github.com/nvbn/subman"
                                   :target "_blank"}
                               [:i.fa.fa-github] " github"]]
   :else [:div.container.col-xs-12.info-box
          [:h2 "Nothing found for \"" @query "\""]]))

(defn search-page
  "Search page view"
  [] (let [query (atom "")
           results (atom [])
           counter (atom 0)]
       (add-watch query :search-request
                  (fn [key ref old-value new-value]
                    (let [current (swap! counter inc)]
                      (go (let [url (str "/api/search/?query=" new-value)
                                response (<! (http/get url))]
                            (when (= current @counter)
                              (reset! results (read-string (:body response)))))))))
       (init-history query)
       [:div [search-box {:value query}]
        [result-list {:items results
                      :query query
                      :counter counter}]]))
