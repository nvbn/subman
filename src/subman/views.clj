(ns subman.views
  (:use [hiccup core page]))

(defn index-page []
  (html5 {:ng-app "subman"}
         [:head
          [:title "Subman - subtitle search api"]
          (include-css "/components/bootstrap/dist/css/bootstrap.css"
                       "/components/bootstrap/dist/css/bootstrap-theme.css")
          (include-js "/components/jquery/jquery.js"
                      "/components/bootstrap/dist/js/bootstrap.js"
                      "/components/angular/angular.js"
                      "/main.js")]
         [:body {:ng-controller "Search"}
          [:div.container
           [:h1 "Subman"]
           [:input.form-contol.col-xs-12 {:type "search"
                                          :placeholder "Type search query"
                                          :ng-model "query"
                                          :ng-change "updateFilter()"}]
           [:div.search-result-list
            [:div.search-result.well {:ng-repeat "result in results"}
             [:h3 [:a {:href "{{result.url}}"
                       :target "_blank"}
                   "{{result.show}} - {{result.name}} (S{{result.season}}E{{result.episode}})"]]
             [:p "Language: {{result.lang}}"]
             [:p "Version: {{result.version}}"]]]]]))
