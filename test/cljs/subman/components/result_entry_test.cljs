(ns subman.components.result-entry-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest done testing is]]
            [test-sugar.core :refer [is=]]
            [cljs.core.async :refer [<!]]
            [om.core :as om :include-macros true]
            [jayq.core :refer [$]]
            [subman.const :as const]
            [subman.helpers :refer [render-node]]
            [subman.components.result-entry :as c]))

(deftest test-get-result-entry-title
  (testing "without name"
    (is= "American Dad" (c/get-result-entry-title {:show "American Dad"})))
  (testing "with name"
    (is= "Simpsons - Episode" (c/get-result-entry-title {:show "Simpsons"
                                                         :name "Episode"}))))

(deftest test-get-result-season-episode
  (testing "without season or episode"
    (is= "" (c/get-result-season-episode {})))
  (testing "with season and episode"
    (is= " S02E12" (c/get-result-season-episode {:season "02"
                                                 :episode "12"}))))

(deftest test-get-result-source
  (is= (str "Source: " (const/type-names const/type-addicted))
       (c/get-result-source {:source const/type-addicted})))

(deftest test-get-result-lang
  (is= "Language: English" (c/get-result-lang {:lang "English"})))

(deftest test-get-result-version
  (testing "without version"
    (is= "" (c/get-result-version {})))
  (testing "with version"
    (is= "Version: LOL" (c/get-result-version {:version "LOL"}))))

(deftest ^:async test-result-entry
  (go (let [[_ $el] (<! (render-node c/result-entry
                                     {:show "Dads"
                                      :name "Testing"
                                      :season "10"
                                      :episode "23"
                                      :source const/type-addicted
                                      :lang "English"
                                      :version "LOL"}))
            html (.html $el)]
        (is (re-find #"Dads - Testing" html))
        (is (re-find #"S10E23" html))
        (is (re-find #"Source: Addicted" html))
        (is (re-find #"Language: English" html))
        (is (re-find #"Version: LOL" html)))
      (done)))
