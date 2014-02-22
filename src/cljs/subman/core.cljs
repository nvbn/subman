(ns subman.core
  (:require [subman.search :as search]))

(defn ^:export run
  "Render base page component"
  []
  (search/search-controller))
