(ns subman.history-test
  (:require [cemerick.cljs.test :refer-macros [deftest is]]
            [reagent.core :refer [atom]]
            [subman.history :as history]))

(deftest test-get-history
  (is (history/get-history)))

(deftest test-init-history
  (is (history/init-history (atom "test"))))
