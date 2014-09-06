(ns subman.parser.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.core.async :as async :refer [<!!]]
            [test-sugar.core :refer [is=]]
            [subman.models :as models]
            [subman.parser.sources.addicted :as addicted]
            [subman.parser.sources.podnapisi :as podnapisi]
            [subman.parser.sources.opensubtitles :as opensubtitles]
            [subman.parser.sources.subscene :as subscene]
            [subman.parser.sources.notabenoid :as notabenoid]
            [subman.parser.sources.uksubtitles :as uksubtitles]
            [subman.const :as const]
            [subman.parser.core :as parser]))

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
    (is= (#'parser/get-new-for-page new-getter #{:exists} 1)
         [:fresh :fresh]))
  (testing "for page without new"
    (is= (#'parser/get-new-for-page new-getter #{:exists} 3)
         [])))

(deftest test-get-new-subtitles-in-chan
  (let [ch (#'parser/get-new-subtitles-in-chan new-getter #{:exists})]
    (is= :fresh (<!! ch))
    (is= :fresh (<!! ch))
    (is= :fresh (<!! ch))
    (is= nil (<!! ch))))

(deftest test-update-all
  (with-redefs [subscene/get-release-page-result (constantly [])
                opensubtitles/get-release-page-result (constantly [])
                ;addicted/get-release-page-result (constantly [])
                podnapisi/get-release-page-result (constantly [])
                notabenoid/get-release-page-result (constantly [])
                uksubtitles/get-release-page-result (constantly [])]
    (parser/update-all)))
