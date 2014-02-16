(ns subman.search
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.reader :refer [read-string]]
            [reagent.core :refer [atom]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [subman.const :as const]
            [subman.helpers :refer [is-filled?]]
            [subman.history :refer [init-history]]
            [subman.push :refer [init-push]]))

(defn search-box
  "Search box"
  [{:keys [value]}] [:div.input-group.input-group-lg.col-xs-12.search-input
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
  [{:keys [query items counter total-count]}]
  (cond
   (> (count @items) 0) [:div.container.col-xs-12
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
                               " and "
                               [:a {:href "http://www.podnapisi.net/"
                                    :target "_blank"} "podnapisi.net"]
                               "."]
                              [:p "Total indexed subtitles count: "
                               @total-count
                               "."]
                              [:a {:href "https://github.com/nvbn/subman"
                                   :target "_blank"}
                               [:i.fa.fa-github] " github"]]
   :else [:div.container.col-xs-12.info-box
          [:h2 "Nothing found for \"" @query "\""]]))

(defn watch-to-query
  "Watch to search query"
  [query results counter]
  (add-watch query :search-request
             (fn [key ref old-value new-value]
               (let [current (swap! counter inc)]
                 (go (let [url (str "/api/search/?query=" new-value)
                           response (<! (http/get url))]
                       (when (= current @counter)
                         (reset! results (read-string (:body response))))))))))

(defn update-total-count
  "Update total count value"
  [total-count] (go (->> (http/get "/api/count/")
                         <!
                         :body
                         read-string
                         (reset! total-count))))

(defn search-page
  "Search page view"
  [] (let [query (atom "")
           results (atom [])
           counter (atom 0)
           total-count (atom 0)]
       (watch-to-query query results counter)
       (update-total-count total-count)
       (init-history query)
       (init-push total-count)
       [:div [search-box {:value query}]
        [result-list {:items results
                      :query query
                      :counter counter
                      :total-count total-count}]]))
