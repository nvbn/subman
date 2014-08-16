(ns subman.components-test
  (:require [cemerick.cljs.test :refer-macros [deftest is]]
            [reagent.core :refer [atom]]
            [subman.const :as const]
            [subman.components :as components]))

(deftest test-search-box
  (is (components/search-box (atom "test"))))

(deftest test-result-line
  (is (components/result-line {:name "name"
                               :show "show"
                               :season "1"
                               :episode "2"
                               :source const/type-subscene
                               :lang "English"
                               :version "hd"})))

(deftest test-result-list
  (is (components/result-list (atom "test")
                              (atom [])
                              (atom 1)
                              (atom 10)
                              (atom false)
                              {:languages (atom "")
                               :current-language (atom "")
                               :sources (atom "")
                               :current-source (atom "")})))
