(ns subman.parser.base-test
  (:require [clojure.test :refer [deftest testing]]
            [test-sugar.core :refer [is=]]
            [subman.parser.base :as b]))

(b/defsource test-source
  :type-id -1
  :get-htmls-for-parse (fn [_] [nil :html])
  :get-subtitles (fn [_] [nil {:a 1} nil {:b 2}]))

(deftest test-source-name
  (is= (.source-name test-source) "test-source"))

(deftest test-download-enabled
  (is= (.download-enabled? test-source) true))

(deftest test-get-htmls-for-parse
  (is= (.get-htmls-for-parse test-source 1)
       [:html]))

(deftest test-get-type
  (is= (.get-type test-source) -1))

(deftest test-get-subtitles
  (is= (.get-subtitles test-source "")
       [{:a 1 :source -1}
        {:b 2 :source -1}]))
