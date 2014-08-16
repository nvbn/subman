(ns subman.filler-test
  (:require [clojure.test :refer [deftest testing is]]
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

(deftest test-get-new-before-seq
  (testing "for all"
    (is= (#'filler/get-new-before-seq new-getter #{:exists})
         [:fresh :fresh :fresh]))
  (testing "for page without results"
    (is= [] (#'filler/get-new-before-seq new-getter #{:exists} 3)))
  (testing "for page greater than update deep"
    (is= [] (#'filler/get-new-before-seq new-getter #{:exists}
                                         (inc const/update-deep)))))

(deftest test-get-new-before
  (is= 3 (count (#'filler/get-new-before new-getter #{:exists}))))

(deftest test-get-all-new
  (is= 9 (count (#'filler/get-all-new #{:exists}
                                      new-getter
                                      new-getter
                                      new-getter))))

(deftest test-update-all
  (with-redefs [subscene/get-release-page-result (constantly [])
                opensubtitles/get-release-page-result (constantly [])
                addicted/get-release-page-result (constantly [])
                podnapisi/get-release-page-result (constantly [])
                notabenoid/get-release-page-result (constantly [])
                uksubtitles/get-release-page-result (constantly [])]
    (is (filler/update-all))))
