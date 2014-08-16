(ns subman.helpers-test
  (:require [cemerick.cljs.test :refer-macros [deftest testing]]
            [test-sugar.core :refer [is=]]
            [subman.helpers :as helpers]))

(deftest test-is-filled?
  (testing "not when nil"
    (is= false (helpers/is-filled? nil)))
  (testing "not when blank"
    (is= false (helpers/is-filled? "")))
  (testing "or yes"
    (is= true (helpers/is-filled? "test"))))

(deftest test-add-0-if-need
  (testing "if length = 1"
    (is= "03" (helpers/add-0-if-need "3")))
  (testing "if length = 1 and number passed"
    (is= "03" (helpers/add-0-if-need 3)))
  (testing "not if other lenght"
    (is= "12" (helpers/add-0-if-need "12"))))

(deftest test-format-season-episode
  (testing "if only season"
    (is= "S02" (helpers/format-season-episode 2 nil)))
  (testing "if only episode"
    (is= "E12" (helpers/format-season-episode nil 12)))
  (testing "if season and episode"
    (is= "S12E02" (helpers/format-season-episode 12 2)))
  (testing "if nothing"
    (is= "" (helpers/format-season-episode nil nil))))
