(ns subman.components.welcome
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [subman.components.edit-option :refer [edit-option]]))

(defcomponent welcome [{:keys [total-count options]} _]
  (display-name [_] "Welcome")
  (render [_] (html [:div.welcome.container.col-xs-12.info-box.form-inline
                     [:h2 "Welcome to subman.io!"]
                     [:p "We index "
                      [:a {:href "http://www.addic7ed.com/"
                           :target "_blank"} "addic7ed.com"]
                      ", "
                      [:a {:href "http://www.opensubtitles.org/"
                           :target "_blank"} "www.opensubtitles.org"]
                      ", "
                      [:a {:href "http://www.podnapisi.net/"
                           :target "_blank"} "podnapisi.net"]
                      ", "
                      [:a {:href "http://subscene.com/"
                           :target "_blank"} "subscene.com"]
                      ", "
                      [:a {:href "http://notabenoid.com/"
                           :target "_blank"} "notabenoid.com"]
                      " and "
                      [:a {:href "http://uksubtitles.ru/"
                           :target "_blank"} "uksubtitles.ru"]
                      "."]
                     [:p "You can specify subtitle language in your query using "
                      [:code ":lang name"]
                      ". Default language used: "
                      (om/build edit-option (:language options))]
                     [:p "And source using "
                      [:code ":source name"]
                      ", by default used: "
                      (om/build edit-option (:source options))]
                     [:p "And you can filter by season and episode with "
                      [:code "S01E01"]
                      " format."]
                     [:p "Total indexed subtitles count: "
                      total-count
                      "."]
                     [:a {:href "https://github.com/nvbn/subman"
                          :target "_blank"}
                      [:i.fa.fa-github]
                      " github"]])))
