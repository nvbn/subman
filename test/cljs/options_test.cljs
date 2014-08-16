(ns subman.options-test
  (:require [cemerick.cljs.test :refer-macros [deftest testing]]
            [test-sugar.core :refer [is=]]
            [goog.testing.storage.FakeMechanism]
            [subman.options :as options]))

(deftest test-get-option
  (testing "when present"
    (let [storage (goog.testing.storage.FakeMechanism.)]
      (.set storage "key" "value")
      (is= "value" @(options/get-option storage "key" "nothing"))))
  (testing "when not"
    (is= "default" @(options/get-option (goog.testing.storage.FakeMechanism.)
                                        "key"
                                        "default")))
  (testing "when nil"
    (let [storage (goog.testing.storage.FakeMechanism.)]
      (.set storage "key" nil)
      (is= "default" @(options/get-option storage "key" "default")))))
