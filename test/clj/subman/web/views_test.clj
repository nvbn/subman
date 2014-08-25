(ns subman.web.views-test
  (:require [clojure.test :refer [deftest testing is]]
            [environ.core :as environ]
            [test-sugar.core :refer [is-do]]
            [subman.web.views :as views]
            [subman.models :as models]))

(deftest test-index-page
  (testing "should require using goog when is debug"
    (with-redefs [environ/env (fn [_] true)]
      (is (re-find #"goog/base" (views/index-page)))))
  (testing "should not require using goog on production"
    (with-redefs [environ/env (fn [_] false)]
      (is-do nil? (re-find #"goog/base" (views/index-page))))))

(deftest test-sitemap-page
  (let [orig @models/unique-show-season-episode]
    (reset! models/unique-show-season-episode #{["american dad" "10" "12"]
                                                ["the double" "" ""]})
    (let [content (views/sitemap-page)]
      (is (re-find #"american%20dad%20S10E12</loc>" content))
      (is (re-find #"the%20double</loc>" content)))
    (reset! models/unique-show-season-episode orig)))
