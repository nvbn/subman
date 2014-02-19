(ns subman.t-helpers
  (:require-macros [purnam.test.sweet :refer [fact facts]])
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
