(ns subman.parser.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.core.async :as async :refer [<!!]]
            [test-sugar.core :refer [is=]]
            [clj-di.core :refer [with-registered]]
            [subman.models :as models]
            [subman.parser.base :refer [defsource]]
            [subman.const :as const]
            [subman.helpers :refer [with-atom]]
            [subman.parser.core :as parser]))

(defsource test-source
  :type-id -1
  :get-htmls-for-parse (fn [x] [{:content x
                                :url "url"}])
  :get-subtitles (fn [x url]
                   (case x
                     4 [{:exists true}]
                     3 [{:exists true}]
                     2 [{:exists false} {:exists true}]
                     1 [{:exists false} {:exists false}]
                     :default [(do
                                 (println x)
                                 x)]))
  :make-url identity)

(deftest test-get-new-for-page
  (testing "for page with new"
    (is= (#'parser/get-new-for-page test-source (complement :exists) 1)
         [{:exists false :source -1} {:exists false :source -1}]))
  (testing "for page without new"
    (is= (#'parser/get-new-for-page test-source (complement :exists) 3)
         [])))

(deftest test-get-new-subtitles-in-chan
  (let [ch (#'parser/get-new-subtitles-in-chan test-source (complement :exists))]
    (is= {:exists false :source -1} (<!! ch))
    (is= {:exists false :source -1} (<!! ch))
    (is= {:exists false :source -1} (<!! ch))
    (is= nil (<!! ch))))

(deftest test-load-new-subtitles
  (with-registered [:sources []]
    (parser/load-new-subtitles)))
