(ns subman.t-options
  (:require-macros [purnam.test :refer [facts fact]])
  (:require [goog.testing.storage.FakeMechanism]
            [subman.options :as options]))

(facts "get option value"
       (fact "when present"
             (let [storage (goog.testing.storage.FakeMechanism.)]
               (.set storage "key" "value")
               @(options/get-option storage "key" "nothing")) => "value")
       (fact "when not"
             @(options/get-option (goog.testing.storage.FakeMechanism.)
                                  "key"
                                  "default") => "default")
       (fact "when nil"
             (let [storage (goog.testing.storage.FakeMechanism.)]
               (.set storage "key" nil)
               @(options/get-option storage "key" "default")) => "default"))
