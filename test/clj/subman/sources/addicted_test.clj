(ns subman.sources.addicted-test
  (:require [clojure.test :refer [deftest testing]]
            [net.cgrand.enlive-html :as html]
            [subman.core-test :refer [is= is-do with-provided]]
            [subman.sources.addicted :as addicted]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]))

(defn get-single-episode
  "Get parsed html for single episode"
  []
  (get-from-file "test/fixtures/subman/sources/addicted_episode.html"))

(deftest test-make-url
  (testing "when start with /"
    (is= (#'addicted/make-url "/test/") "http://www.addic7ed.com/test/"))
  (testing "when not"
    (is= (#'addicted/make-url "test/") "http://www.addic7ed.com/test/")))

(deftest test-is-version-line?
  (testing "when version line passed"
    (is-do true? (#'addicted/is-version-line? (get-from-line
                                               "<td class='NewsTitle'><img src='/images/folder_page.png' /></td>"))))
  (testing "when not"
    (is-do nil? (#'addicted/is-version-line? (get-from-line "<td></td>")))))

(deftest test-is-language-line?
  (testing "when language line passed"
    (is-do true? (#'addicted/is-language-line?
                  (get-from-line "<td class='language'><a href='#'><b></b></a></td>"))))
  (testing "when not"
    (is-do false? (#'addicted/is-language-line? (get-from-line "<td></td>")))))

(deftest test-get-version
  (testing "when has name"
    (is= (#'addicted/get-version (get-from-line
                                  "<td class='NewsTitle'><b><span></span>test</b></td>"))
         {:name "test"
          :langs []}))
  (testing "when not"
    (is= (#'addicted/get-version (get-from-line
                                  "<td class='NewsTitle'><b><span></span></b></td>"))
         {:name ""
          :langs []})))

(deftest test-get-language
  (is= (#'addicted/get-lang (get-from-line
                             "<td class='language'><span>test</span></td>
                             <td><a href='test-url' class='buttonDownload'></a></td>"))
       {:name "test"
        :url "http://www.addic7ed.com/test-url"}))

(deftest test-add-lang
  (with-provided {#'addicted/get-lang (constantly "test")}
    (is= (#'addicted/add-lang "" {:langs []})
         {:langs ["test"]})))

(deftest test-get-subtitles
  (with-provided {#'addicted/is-version-line? #(= % 1)
                  #'addicted/get-version (constantly {:name "test"
                                                      :langs []})
                  #'addicted/is-language-line? #(= % 2)
                  #'addicted/add-lang (constantly {:name "test"
                                                   :langs ["us"]})}
    (is= (#'addicted/get-subtitles [1 2])
         [{:name "test"
           :langs ["us"]}])))

(deftest test-get-version
  (with-provided {#'helpers/fetch (constantly (get-single-episode))}
    (testing "return all versions"
      (is= 2 (count (#'addicted/get-versions {:url ""}))))
    (testing "return all languages"
      (is= 3 (-> (#'addicted/get-versions {:url ""})
                 first
                 :langs
                 count)))))

(deftest test-get-release-url
  (is= (#'addicted/get-releases-url 1)
       "http://www.addic7ed.com/log.php?mode=versions&page=1"))

(deftest test-episode-from-release
  (is= (#'addicted/episode-from-release (-> (get-from-file "test/fixtures/subman/sources/addicted_line.html")
                                            (html/select [:a])
                                            first))
       {:episode "6"
        :name "Episode Six"
        :season "3"
        :show "Episodes"
        :url "http://www.addic7ed.com/serie/Episodes/3/6/Episode_Six"}))

(deftest test-get-release-page-result
  (with-provided {#'helpers/fetch #(if (= % (#'addicted/get-releases-url 1))
                                     (get-from-file "test/fixtures/subman/sources/addicted_release.html")
                                     (get-from-file "test/fixtures/subman/sources/addicted_episode.html"))}
    (is= (-> (#'addicted/get-release-page-result 1)
             first
             :name)
         "Family Affair")))
