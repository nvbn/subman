(ns subman.views
  (:use [hiccup core page]))

(defn index-page []
  (html5 {:ng-app "subman"}
         [:head
          [:title "Subman - subtitle search service with api"]
          (include-css "/components/bootstrap/dist/css/bootstrap.css"
                       "/components/bootstrap/dist/css/bootstrap-theme.css"
                       "/components/font-awesome/css/font-awesome.css")
          (include-js "/components/jquery/jquery.js"
                      "/components/bootstrap/dist/js/bootstrap.js"
                      "/main.js")]
         [:body
          [:div.input-group.input-group-lg.col-xs-12 {:data-spy "affix"
                                                      :data-offset-top "40"
                                                      :style "z-index: 100"}
           [:span.input-group-addon [:i.fa.fa-search]]
           [:input.form-control {:type "text"
                                 :placeholder "Type search query"
                                 :ng-model "query"
                                 :ng-change "updateFilter()"}]]
          [:div.container.col-xs-12
           [:div.search-result-list.list-group
            [:a.list-group-item.search-result {:href "{{result.url}}"
                                               :target "_blank"
                                               :ng-repeat "result in results"}
             [:h3
              "{{result.show}}"
              [:span {:ng-show "result.name"} " - {{result.name}}"]
              [:span {:ng-show "result.season || result.episode"}
               " (" [:span {:ng-show "result.season"} "S{{result.season}}"]
               [:span {:ng-show "result.episode"} "E{{result.episode}}"]
               ")"]]
             [:p.pull-right "Source: {{{0: 'Addicted', 1: 'Podnapisi'}[result.source]}}"]
             [:p "Language: {{result.lang}}"]
             [:p {:ng-show "result.version"} "Version: {{result.version}}"]]]]
          [:script "subman.core.run();"]]))
