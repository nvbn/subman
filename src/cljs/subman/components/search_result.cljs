(ns subman.components.search-result
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [subman.const :as const]
            [subman.components.result-entry :refer [result-entry]]))

(defn search-resul-list
  "Render search result when something found"
  [results]
  (html [:div.container.col-xs-12.search-result-holder
         [:div.search-result.search-result-list.list-group
          (map-indexed #(om/build result-entry %2
                                  {:react-key (str "search-result-" %1)})
                       results)]]))

(defn info-box
  "Render information box in search result"
  [text]
  (html [:div.container.col-xs-12.info-box
         [:h2 text]]))

(defcomponent search-result [{:keys [stable-search-query results in-progress]} _]
              (display-name [_] "Search Result")
              (render [_]
                      (cond
                        (pos? (count results)) (search-resul-list results)
                        in-progress (info-box "Searching...")
                        :else (info-box (str "Nothing found for \"" stable-search-query "\"")))))
