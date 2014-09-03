(ns subman.models-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :refer-macros [deftest testing is done]]
            [test-sugar.core :refer [is=]]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]
            [subman.const :as const]
            [subman.deps :as d]
            [subman.models :as m]))

(deftest test-create-search-url
  (reset! d/sources const/type-names)
  (testing "with query"
    (is= "/api/search/?lang=english&source=-1&query=test&offset=0"
         (m/create-search-url "test" 0 "english" "all")))
  (testing "with query and lang"
    (is= "/api/search/?lang=ru&source=-1&query=test&offset=0"
         (m/create-search-url "test :lang ru" 0 "english" "all")))
  (testing "with offset"
    (is= "/api/search/?lang=english&source=-1&query=test&offset=100"
         (m/create-search-url "test" 100 "english" "all")))
  (testing "with source"
    (is= "/api/search/?lang=english&source=0&query=test&offset=0"
         (m/create-search-url "test :source addicted" 0 "english" "all")))
  (testing "with source and lang"
    (is= "/api/search/?lang=uk&source=0&query=test&offset=0"
         (m/create-search-url "test :source addicted :lang uk" 0 "english" "all"))))

(deftest test-get-source-id
  (reset! d/sources const/type-names)
  (testing "for source"
    (is= 0 (m/get-source-id "addicted")))
  (testing "for source in wrong case"
    (is= 1 (m/get-source-id "podNApisi")))
  (testing "with source = all"
    (is= -1 (m/get-source-id "all")))
  (testing "with wrong source"
    (is= -2 (m/get-source-id "wtf-this-source"))))

(deftest ^:async test-get-search-result
  (go (reset! d/http-get (fn [url]
                           (go (when (= url "/api/search/?lang=uk&source=0&query=test&offset=0")
                                 {:body [:test-search-result]}))))
      (is (= (<! (m/get-search-result "test :source addicted :lang uk"
                                      0 "english" "all"))
             [:test-search-result]))
      (done)))

(deftest ^:async test-get-total-count
  (go (reset! d/http-get (fn [_]
                           (go {:body {:total-count 999}})))
      (is (= 999 (<! (m/get-total-count))))
      (done)))

(deftest ^:async test-get-languages
  (go (reset! d/http-get (fn [_]
                           (go {:body [{:term "english"}
                                       {:term "spanish"}
                                       {:term "russian"}]})))
      (is (= (<! (m/get-languages))
             ["english" "spanish" "russian"])))
  (done))

(deftest ^:async test-get-sources
  (go (reset! d/sources {:addicted "Addicted"
                         :opensubtitles "opensubtitles"})
      (is (= (apply hash-set (<! (m/get-sources)))
             #{"addicted" "opensubtitles"}))
      (done)))
