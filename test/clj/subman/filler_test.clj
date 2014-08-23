(ns subman.filler-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.core.async :as async :refer [<!!]]
            [test-sugar.core :refer [is=]]
            [subman.models :as models]
            [subman.sources.addicted :as addicted]
            [subman.sources.podnapisi :as podnapisi]
            [subman.sources.opensubtitles :as opensubtitles]
            [subman.sources.subscene :as subscene]
            [subman.sources.notabenoid :as notabenoid]
            [subman.sources.uksubtitles :as uksubtitles]
            [subman.const :as const]
            [subman.filler :as filler]))

(defn new-getter
  "Fake getter for tests"
  [page]
  (case page
    4 [:exists]
    3 [:exists]
    2 [:fresh :exists]
    1 [:fresh :fresh]
    :default [(do
                (println page)
                page)]))

(deftest test-get-new-for-page
  (testing "for page with new"
    (is= (#'filler/get-new-for-page new-getter #{:exists} 1)
         [:fresh :fresh]))
  (testing "for page without new"
    (is= (#'filler/get-new-for-page new-getter #{:exists} 3)
         [])))

(deftest test-get-new-subtitles-in-chan
  (let [ch (#'filler/get-new-subtitles-in-chan new-getter #{:exists})]
    (is= :fresh (<!! ch))
    (is= :fresh (<!! ch))
    (is= :fresh (<!! ch))
    (is= nil (<!! ch))))

(deftest test-update-all
  (with-redefs [subscene/get-release-page-result (constantly [])
                opensubtitles/get-release-page-result (constantly [])
                addicted/get-release-page-result (constantly [])
                podnapisi/get-release-page-result (constantly [])
                notabenoid/get-release-page-result (constantly [])
                uksubtitles/get-release-page-result (constantly [])]
               (filler/update-all)))
