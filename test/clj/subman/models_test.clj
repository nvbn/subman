(ns subman.models-test
  (:require [clojure.test :refer [deftest testing]]
            [clojurewerkz.elastisch.rest.document :as esd]
            [test-sugar.core :refer [is= is-do with-provided]]
            [subman.const :as const]
            [subman.models :as models]))

(deftest test-update-total-count
  (let [orig (atom @models/total-count)]
    (try (with-provided {#'esd/search (constantly {:hits {:total 10}})}
           (is= (models/update-total-count) 10)
           (is= @models/total-count 10))
      (finally (reset! models/total-count @orig)))))

(deftest test-get-season-episode-parts
  (testing "with SnEn notation"
    (is= (#'models/get-season-episode-parts "test s01e10")
         ["s01e10" "01" "10"]))
  (testing "with nxn notation"
    (is= (#'models/get-season-episode-parts "test 01x10")
         ["01x10" "01" "10"]))
  (testing "or nil"
    (is-do nil? (#'models/get-season-episode-parts "test"))))

(deftest test-get-season-episode
  (testing "with season and episode"
    (is= (#'models/get-season-episode "test s01e01")
         [{:term {:season "1"}} {:term {:episode "1"}}]))
  (testing "without"
    (is= (#'models/get-season-episode "test") [])))

(deftest test-build-query
  (testing "should build query"
    (is= (#'models/build-query "Dads.2013.S01E18.HDTV.x264-EXCELLENCE[rartv]"
                               "en" const/type-all)
         [:query {:bool {:must
                         [{:fuzzy_like_this
                           {:boost 5
                            :fields [:show :name]
                            :like_text "Dads 2013 S01E18 HDTV x264-EXCELLENCE[rartv]"}}
                          {:term {:season "1"}}
                          {:term {:episode "18"}}]
                         :should {:fuzzy_like_this
                                  {:boost 2
                                   :fields [:version]
                                   :like_text "Dads 2013 S01E18 HDTV x264-EXCELLENCE[rartv]"}}}}
          :filter {:term {:lang "en"}} :size 100]))
  (testing "should build query with filter by source"
    (is= (#'models/build-query "query" "ru" const/type-addicted)
         [:query {:bool {:must
                         [{:fuzzy_like_this
                           {:boost 5
                            :fields [:show :name]
                            :like_text "query"}}
                          {:term {:source 0}}]
                         :should {:fuzzy_like_this
                                  {:boost 2
                                   :fields [:version]
                                   :like_text "query"}}}}
          :filter {:term {:lang "ru"}} :size 100])))

(deftest test-search
  (with-provided {#'esd/search (fn [_ _ _ & {:keys [from size] :as _}]
                                 (when (and (= from 10) (= size const/result-size))
                                   {:hits {:hits [{:_source "test"}]}}))}
    (is= ["test"] (models/search :query "test"
                                 :offset 10
                                 :lang "en"))))

(deftest test-in-db
  (with-provided {#'esd/search (fn [_ _ _ & {:keys [filter]}]
                                 (when (= filter {:term {:url "test"}})
                                   {:hits {:total 5}}))}
    (is-do true? (models/in-db {:url "test"}))))

(deftest test-list-languages
  (with-provided {#'esd/search (fn [& _] {:facets {:tag {:terms [{:term "english"
                                                                  :count 100}
                                                                 {:term "russian"
                                                                  :count 50}]}}})}
    (is= (models/list-languages) [{:term "english"
                                   :count 100}
                                  {:term "russian"
                                   :count 50}])))
