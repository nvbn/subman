(ns subman.autocomplete-test
  (:require [cemerick.cljs.test :refer-macros [deftest testing]]
            [test-sugar.core :refer [is=]]
            [subman.autocomplete :as autocomplete]))

(deftest test-only-contains
  (is= (autocomplete/only-contains ["cat" "Cats" "dog"]
                                   "cat")
       ["cat" "Cats"]))

(deftest test-with-value
  (is= (autocomplete/with-value ["cat" "eat"] ["water" "tests"])
       ["cat eat water" "cat eat tests"]))

(deftest test-with-value-contains
  (is= (autocomplete/with-value-contains ["something" "eat" "cat"]
         ["food" "Fish" "cat"] "f")
       ["cat eat food" "cat eat Fish"]))

(def langs ["en" "ru"])

(def sources ["test" "cat"])

(deftest test-get-completion
  (testing "when not need"
    (is= [] (autocomplete/get-completion "don't need"
                                         langs
                                         sources)))
  (testing "for lang all"
    (is= (autocomplete/get-completion "some :lang"
                                      langs
                                      sources)
         ["some :lang en" "some :lang ru"]))
  (testing "for lang only match"
    (is= (autocomplete/get-completion "some :lang en"
                                      langs
                                      sources)
         ["some :lang en"]))
  (testing "for source all"
    (is= (autocomplete/get-completion "some :source"
                                      langs
                                      sources)
         ["some :source test" "some :source cat"]))
  (testing "for source only match"
    (is= (autocomplete/get-completion "some :source cat"
                                      langs
                                      sources)
         ["some :source cat"]))
  (testing "for keywords"
    (is= (autocomplete/get-completion "some :"
                                      langs
                                      sources)
         ["some :lang" "some :source"])))
