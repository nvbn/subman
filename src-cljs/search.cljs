(ns subman.search
  (:use [cljs.reader :only [read-string]]
        [reagent.core :only [atom]]
        [subman.helpers :only [is-filled?]])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [subman.const :as const]))

(defn search-box
  "Search box"
  [{:keys [value]}] [:div.input-group.input-group-lg.col-xs-12 {:data-spy "affix"
                                                                :data-offset-top "40"
                                                                :style {:z-index 100}}
                     [:span.input-group-addon [:i.fa.fa-search]]
                     [:input.form-control {:type "text"
                                           :placeholder "Type search query"
                                           :on-change #(reset! value (-> % .-target .-value))}]])

(defn result-line
  "Search result line"
  [{:keys [url, name, show, season, episode, source, lang, version]}]
  [:a.list-group-item.search-result {:href "{{result.url}}"
                                     :target "_blank"}
   [:h3
    show (when (is-filled? name) (str " - " name))
    (when (some is-filled? [season episode])
      [:span " " (when (is-filled? season) (str "S" season))
       (when (is-filled? episode) (str "E" episode))])]
   [:p.pull-right "Source: " (cond
                              (= source const/type-addicted) "Addicted"
                              (= source const/type-podnapisi) "Podnapisi")]
   [:p "Language:" lang]
   (when (is-filled? version)
     [:p "Version: " version])])

(defn result-list
  "Search result list"
  [{:keys [items]}] [:div.container.col-xs-12
                     [:div.search-result-list.list-group (for [item @items]
                                                           (result-line item))]])

(defn search-page
  "Search page view"
  [] (let [query (atom "")
           results (atom [])]
       (add-watch query :search-request
                  (fn [key ref old-value new-value]
                    (go (let [url (str "/api/search/?query=" new-value)
                              response (<! (http/get url))]
                          (reset! results (read-string (:body response)))))))
       [:div [search-box {:value query}]
        [result-list {:items results}]]))
