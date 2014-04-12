(ns subman.t-autocomplete
  (:require-macros [purnam.test :refer [facts fact]])
  (:require [subman.autocomplete :as autocomplete]))

(fact "should return only contains"
      (autocomplete/only-contains ["cat"
                                   "Cats"
                                   "dog"] "cat") => ["cat" "Cats"])

(fact "should return with added value"
      (autocomplete/with-value ["cat" "eat"]
                               ["water" "tests"]) => ["cat eat water"
                                                      "cat eat tests"])

(fact "should return with value only contains needle"
      (autocomplete/with-value-contains ["something" "eat" "cat"]
                                        ["food" "Fish" "cat"]
                                        "f") => ["cat eat food"
                                                 "cat eat Fish"])

(def langs ["en" "ru"])

(def sources ["test" "cat"])

(facts "get autocomplete"
       (fact "when not need"
             (autocomplete/get-completion "don't need"
                                          langs
                                          sources) => [])
       (fact "for lang all"
             (autocomplete/get-completion "some :lang"
                                          langs
                                          sources) => ["some :lang en"
                                                       "some :lang ru"])
       (fact "for lang only match"
             (autocomplete/get-completion "some :lang en"
                                          langs
                                          sources) => ["some :lang en"])
       (fact "for source all"
             (autocomplete/get-completion "some :source"
                                          langs
                                          sources) => ["some :source test"
                                                       "some :source cat"])
       (fact "for source only match"
             (autocomplete/get-completion "some :source cat"
                                          langs
                                          sources) => ["some :source cat"])
       (fact "for keywords"
             (autocomplete/get-completion "some :"
                                          langs
                                          sources) => ["some :lang"
                                                       "some :source"]))
