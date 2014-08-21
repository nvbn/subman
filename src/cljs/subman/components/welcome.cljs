(ns subman.components.welcome
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [subman.components.edit-option :refer [edit-option]]))

(defn welcome
  "Component for welcome messaeg"
  [{:keys [total-count options]} owner]
  (om/component
   (dom/div #js {:className "welcome"}
            (dom/h2 nil "Welcome to subman.io!")
            (dom/p nil "We index "
                   (dom/a #js {:href "http://www.addic7ed.com/"
                               :target "_blank"} "addic7ed.com")
                   ", "
                   (dom/a #js {:href "http://www.opensubtitles.org/"
                               :target "_blank"} "www.opensubtitles.org")
                   ", "
                   (dom/a #js {:href "http://www.podnapisi.net/"
                               :target "_blank"} "podnapisi.net")
                   ", "
                   (dom/a #js {:href "http://subscene.com/"
                               :target "_blank"} "subscene.com")
                   ", "
                   (dom/a #js {:href "http://notabenoid.com/"
                               :target "_blank"} "notabenoid.com")
                   " and "
                   (dom/a #js {:href "http://uksubtitles.ru/"
                               :target "_blank"} "uksubtitles.ru")
                   ".")
            (dom/p nil "You can specify subtitle language in your query using "
                   (dom/code nil ":lang name")
                   ". Default language used: "
                   (om/build edit-option (:language options)))
            (dom/p nil "And source using "
                   (dom/code nil ":source name")
                   ", by default used: "
                   (om/build edit-option (:source options)))
            (dom/p nil "And you can filter by season and episode with "
                   (dom/code nil "S01E01")
                   " format.")
            (dom/p nil "Total indexedsubtitles count: "
                   (om/value total-count)
                   ".")
            (dom/a #js {:href "https://github.com/nvbn/subman"
                        :target "_blank"}
                   (dom/i #js {:className "fa fa-github"})
                   " github"))))
