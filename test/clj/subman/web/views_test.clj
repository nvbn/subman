(ns subman.web.views-test
  (:require [clojure.test :refer [deftest testing is]]
            [environ.core :as environ]
            [test-sugar.core :refer [is-do with-provided]]
            [subman.web.views :as views]))

(deftest test-index-page
  (testing "should require using goog when is debug"
    (with-provided {#'environ/env (fn [_] true)}
      (is (re-find #"goog\.require" (views/index-page)))
      (is (re-find #"goog/base" (views/index-page)))))
  (testing "should not require using goog on production"
    (with-provided {#'environ/env (fn [_] false)}
      (is-do nil? (re-find #"goog\.require" (views/index-page)))
      (is-do nil? (re-find #"goog/base" (views/index-page))))))
