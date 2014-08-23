(ns subman.components.result-entry
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [subman.const :as const]
            [subman.helpers :refer [is-filled? format-season-episode]]))

(defn get-result-entry-title
  "Get formatted title for result entry"
  [{:keys [show name]}]
  (str show (if (is-filled? name)
              (str " - " name)
              "")))

(defn get-result-season-episode
  "Get formatted season-episode of result entry"
  [{:keys [season episode]}]
  (if (some is-filled? [season episode])
    (str " " (format-season-episode season episode))
    ""))

(defn get-result-source
  "Get formatted source of result entry"
  [{:keys [source]}]
  (str "Source: " (const/type-names source)))

(defn get-result-lang
  "Get formatted language of result entry"
  [{:keys [lang]}]
  (str "Language: " lang))

(defn get-result-version
  "Get formatted version of result entry"
  [{:keys [version]}]
  (if (is-filled? version)
    (str "Version: " version)
    ""))

(defcomponent result-entry [entry _]
              (display-name [_] "Result Entry")
              (render [_]
                      (html [:a.result-entry.list-group-item.search-result
                             {:href   (om/value (:url entry))
                              :target "_blank"}
                             [:h3 (om/value (get-result-entry-title entry))
                              [:span (om/value (get-result-season-episode entry))]]
                             [:p.pull-right (om/value (get-result-source entry))]
                             [:p (om/value (get-result-lang entry))]
                             [:p (om/value (get-result-version entry))]])))
