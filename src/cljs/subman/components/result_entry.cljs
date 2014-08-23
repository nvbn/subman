(ns subman.components.result-entry
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
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

(defn result-entry
  "Component for single search result entry"
  [entry _]
  (reify
    om/IDisplayName
    (display-name [_] "Result Entry")
    om/IRender
    (render [_]
      (dom/a #js {:href      (om/value (:url entry))
                  :className "result-entry"}
             (dom/h3 nil (om/value (get-result-entry-title entry))
                     (dom/span nil (om/value (get-result-season-episode entry))))
             (dom/p nil (om/value (get-result-source entry)))
             (dom/p nil (om/value (get-result-lang entry)))
             (dom/p nil (om/value (get-result-version entry)))))))
