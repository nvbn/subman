(ns subman.web.views-test
  (:require [clojure.test :refer [deftest testing is]]
            [environ.core :as environ]
            [test-sugar.core :refer [is-do is=]]
            [subman.helpers :refer [with-atom]]
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
  (with-atom [models/unique-show-season-episode [[["american dad" "10" "12"]
                                                  ["the double" "" ""]]]]
    (testing "for exists partiotion"
      (let [content (views/sitemap-page 0)]
        (is-do re-find #"american%20dad%20S10E12</loc>" content)
        (is-do re-find #"the%20double</loc>" content)))
    (testing "for blank partition"
      (is (views/sitemap-page 12)))))

(deftest test-robots-page
  (with-atom [models/unique-show-season-episode [[] []]]
    (is= (views/robots-page)
         (str "User-agent: *\n"
              "Allow: /\n"
              "Sitemap: http://localhost:3000/sitemap.0.xml\n"
              "Sitemap: http://localhost:3000/sitemap.1.xml\n"))))
