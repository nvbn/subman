(ns subman.t-helpers
  (:require-macros [purnam.test :refer [fact facts]])
  (:require [subman.helpers :as helpers]))

(facts "is filled?"
       (fact "not when nil"
             (helpers/is-filled? nil) => false)
       (fact "not when blank"
             (helpers/is-filled? "") => false)
       (fact "or yes"
             (helpers/is-filled? "test") => true))

(facts "truthy?"
       (fact "not when blank"
             (helpers/truthy "") => false)
       (fact "not when []"
             (helpers/truthy []) => false)
       (fact "not when nil"
             (helpers/truthy nil) => false)
       (fact "not when false"
             (helpers/truthy false) => false)
       (fact "or yes"
             (helpers/truthy "test") => true))

(facts "add 0 before number"
       (fact "if length = 1"
             (helpers/add-0-if-need "3") => "03")
       (fact "if length = 1 and number passed"
             (helpers/add-0-if-need 3) => "03")
       (fact "not if other lenght"
             (helpers/add-0-if-need "12") => "12"))

(facts "format season episode"
       (fact "if only season"
             (helpers/format-season-episode 2 nil) => "S02")
       (fact "if only episode"
             (helpers/format-season-episode nil 12) => "E12")
       (fact "if season and episode"
             (helpers/format-season-episode 12 2) => "S12E02")
       (fact "if nothing"
             (helpers/format-season-episode nil nil) => ""))
