(ns subman.sources.addicted-test
  (:require [clojure.test :refer [deftest testing is]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is= is-do]]
            [subman.sources.addicted :as addicted]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]))

(def release-file-name "resources/fixtures/subman/sources/addicted_release.html")

(def release-html (slurp release-file-name))

(def release (get-from-file release-file-name))

(def episode-file-name "resources/fixtures/subman/sources/addicted_episode.html")

(def episode-html (slurp episode-file-name))

(def episode (get-from-file episode-file-name))

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
  (with-redefs [addicted/get-lang (constantly "test")]
    (is= (#'addicted/add-lang "" {:langs []})
         {:langs ["test"]})))

(deftest test-make-url
  (testing "when start with /"
    (is= (#'addicted/make-url "/test/") "http://www.addic7ed.com/test/"))
  (testing "when not"
    (is= (#'addicted/make-url "test/") "http://www.addic7ed.com/test/")))

(deftest test-get-release-url
  (is= (#'addicted/get-releases-url 1)
       "http://www.addic7ed.com/log.php?mode=versions&page=1"))

(deftest test-get-urls-from-list
  (is= (addicted/get-urls-from-list release)
       ["http://www.addic7ed.com/serie/The_Following/2/4/Family_Affair"
        "http://www.addic7ed.com/serie/Midsomer_Murders/16/5/The_Killings_at_Copenhagen"
        "http://www.addic7ed.com/serie/Rick_and_Morty/1/1/Pilot"
        "http://www.addic7ed.com/serie/Lab_Rats_%28US%29/3/1/Sink_or_Swim"
        "http://www.addic7ed.com/serie/Lost_Girl/4/13/Dark_Horse"
        "http://www.addic7ed.com/serie/The_Walking_Dead/4/10/Inmates"
        "http://www.addic7ed.com/serie/Episodes/3/6/Episode_Six"
        "http://www.addic7ed.com/serie/Episodes/3/6/Episode_Six"
        "http://www.addic7ed.com/serie/The_Haunted_Hathaways/1/19/haunted_Crushing"
        "http://www.addic7ed.com/serie/The_Haunted_Hathaways/1/19/haunted_Crushing"]))

(deftest test-get-htmls-for-parse
  (with-redefs [helpers/fetch (fn [_] release)
                helpers/download (fn [_] release-html)]
    (is= (addicted/get-htmls-for-parse 1)
         (repeat 10 release-html))))

(deftest test-get-episode-name-string
  (is= (addicted/get-episode-name-string episode)
       "Raising Hope - 04x12 - Hot Dish"))

(deftest test-get-episode-information
  (is= (addicted/get-episode-information episode)
       {:episode "12"
        :name "Hot Dish"
        :season "4"
        :show "Raising Hope"}))

(deftest test-get-versions
  (testing "return all versions"
    (is= 2 (count (#'addicted/get-versions episode))))
  (testing "return all languages"
    (is= 3 (-> (#'addicted/get-versions episode)
               first
               :langs
               count))))

(deftest test-get-version-langs
  (with-redefs [addicted/is-version-line? #(= % 1)
                addicted/get-version (constantly {:name "test"
                                                  :langs []})
                addicted/is-language-line? #(= % 2)
                addicted/add-lang (constantly {:name "test"
                                               :langs ["us"]})]
    (is= (#'addicted/get-version-langs [1 2])
         [{:name "test"
           :langs ["us"]}])))

(deftest test-get-subtitles
  (is= (addicted/get-subtitles episode-html)
       [{:episode "12"
         :lang "Bulgarian"
         :name "Hot Dish"
         :season "4"
         :show "Raising Hope"
         :source 0
         :url "http://www.addic7ed.com/updated/35/83173/1"
         :version "Version KILLERS, 0.00 MBs "}
        {:episode "12"
         :lang "French"
         :name "Hot Dish"
         :season "4"
         :show "Raising Hope"
         :source 0
         :url "http://www.addic7ed.com/updated/8/83173/1"
         :version "Version KILLERS, 0.00 MBs "}
        {:episode "12"
         :lang "English"
         :name "Hot Dish"
         :season "4"
         :show "Raising Hope"
         :source 0
         :url "http://www.addic7ed.com/original/83173/1"
         :version "Version KILLERS, 0.00 MBs "}
        {:episode "12"
         :lang "English"
         :name "Hot Dish"
         :season "4"
         :show "Raising Hope"
         :source 0
         :url "http://www.addic7ed.com/original/83173/0"
         :version "Version KILLERS, 0.00 MBs "}]))
