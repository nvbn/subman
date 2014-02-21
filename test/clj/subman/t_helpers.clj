(ns subman.t-helpers
  (:require [midje.sweet :refer [fact facts => provided anything]]
            [subman.const :as const]
            [subman.helpers :as helpers]))

(fact "should remove first 0"
      (helpers/remove-first-0 "01") => "1")

(fact "should replace nil to empty string"
      (helpers/nil-to-blank nil) => "")

(fact "should return fallback on exception"
      ((helpers/make-safe #(throw (Exception. %)) :safe) "danger") => :safe)

(facts "get season and episode from string"
       (fact "when appears"
             (helpers/get-season-episode "s01E12") => ["1" "12"])
       (fact "when not"
             (helpers/get-season-episode "0202") => ["" ""]))

(fact "should make paths static"
      (helpers/make-static "test" "path") => [(str const/static-path "test")
                                              (str const/static-path "path")])

(fact "sholud apply with static paths"
      (helpers/as-static identity "test") => (str const/static-path "test"))
