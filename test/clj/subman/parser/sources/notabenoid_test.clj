(ns subman.parser.sources.notabenoid-test
  (:require [clojure.test :refer [deftest testing is]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is= is-do]]
            [subman.const :as const]
            [subman.parser.sources.notabenoid :as notabenoid]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]))

(def release-file-name "resources/fixtures/notabenoid_release.html")

(def release-page (get-from-file release-file-name))

(def release-html (slurp release-file-name))

(def book-filename "resources/fixtures/notabenoid_book.html")

(def book-page (get-from-file book-filename))

(def book-html (slurp book-filename))

(def urls ["http://notabenoid.com/book/50607" "http://notabenoid.com/book/44311"
           "http://notabenoid.com/book/50635" "http://notabenoid.com/book/45802"
           "http://notabenoid.com/book/36436" "http://notabenoid.com/book/38401"
           "http://notabenoid.com/book/49992" "http://notabenoid.com/book/44312"
           "http://notabenoid.com/book/41972" "http://notabenoid.com/book/36828"
           "http://notabenoid.com/book/43718" "http://notabenoid.com/book/45125"
           "http://notabenoid.com/book/43881" "http://notabenoid.com/book/50461"
           "http://notabenoid.com/book/50532" "http://notabenoid.com/book/43420"
           "http://notabenoid.com/book/50147" "http://notabenoid.com/book/50612"
           "http://notabenoid.com/book/44146" "http://notabenoid.com/book/49011"
           "http://notabenoid.com/book/50385" "http://notabenoid.com/book/47140"
           "http://notabenoid.com/book/49875" "http://notabenoid.com/book/50500"
           "http://notabenoid.com/book/36958" "http://notabenoid.com/book/47235"
           "http://notabenoid.com/book/50487" "http://notabenoid.com/book/49212"
           "http://notabenoid.com/book/50615" "http://notabenoid.com/book/50142"
           "http://notabenoid.com/book/50306" "http://notabenoid.com/book/49994"
           "http://notabenoid.com/book/49951" "http://notabenoid.com/book/46612"
           "http://notabenoid.com/book/50552" "http://notabenoid.com/book/44613"
           "http://notabenoid.com/book/50496" "http://notabenoid.com/book/41291"
           "http://notabenoid.com/book/43991" "http://notabenoid.com/book/41235"
           "http://notabenoid.com/book/50129" "http://notabenoid.com/book/49715"
           "http://notabenoid.com/book/48856" "http://notabenoid.com/book/50351"
           "http://notabenoid.com/book/33498" "http://notabenoid.com/book/41604"
           "http://notabenoid.com/book/44167" "http://notabenoid.com/book/50340"
           "http://notabenoid.com/book/42904" "http://notabenoid.com/book/49241"])

(def release-line
  (-> (get-from-line "<a href=\"/book/36828\">Da Vinci's Demons</a>")
      (html/select [:a])
      first))

(def episode-line
  (get-from-file "resources/fixtures/notabenoid_episode.html"))

(def episode-line-not-ready
  (get-from-file "resources/fixtures/notabenoid_not_ready.html"))

(deftest test-make-url
  (is= (#'notabenoid/make-url "/test") "http://notabenoid.com/test"))

(deftest test-get-release-page-url
  (is= (#'notabenoid/get-release-page-url 15)
       "http://notabenoid.com/search/index/t//cat/1/s_lang/0/t_lang/1/ready/1/gen/1/sort/4/Book_page/15"))

(deftest test-book-from-line
  (with-redefs [helpers/download (constantly "content")]
    (is= (#'notabenoid/book-from-line release-line) {:content "content"
                                                     :url "http://notabenoid.com/book/36828"})))

(deftest test-get-book-title
  (is= (#'notabenoid/get-book-title book-page)
       "Shetland"))

(deftest test-episode-ready
  (testing "when ready"
    (is-do (complement nil?) (#'notabenoid/episode-ready? episode-line)))
  (testing "when not"
    (is-do nil? (#'notabenoid/episode-ready? episode-line-not-ready))))

(deftest test-get-episode-name
  (is= (#'notabenoid/get-episode-name "Bones - 09x18 - The Carrot in the Kudzu")
       "The Carrot in the Kudzu"))

(deftest test-episode-title-line
  (is= (#'notabenoid/get-episode-title-line episode-line)
       "Bones - 09x14 - The Master In The Slop"))

(deftest test-get-episode-url
  (is= (#'notabenoid/get-episode-url episode-line)
       "http://notabenoid.com/book/43718/195318/ready"))

(deftest test-episode-from-line
  (is= (#'notabenoid/episode-from-line episode-line)
       {:episode "14"
        :lang "russian"
        :name "The Master In The Slop"
        :season "9"
        :url "http://notabenoid.com/book/43718/195318/ready"
        :version ""}))

(deftest test-episodes-from-book
  (testing "should create for all ready episodes"
    (is= (count (#'notabenoid/episodes-from-book book-page)) 7))
  (testing "should set show name"
    (is= (:show (first (#'notabenoid/episodes-from-book book-page)))
         "Shetland")))

(deftest test-get-htmls-for-parse
  (with-redefs [helpers/fetch (constantly release-page)
                helpers/download (constantly book-html)]
    (is= (notabenoid/get-htmls-for-parse 1)
         (for [url urls] {:url url
                          :content book-html}))))

(deftest test-get-subtitles
  (is= (notabenoid/get-subtitles book-html "")
         [{:episode "1"
           :lang "russian"
           :name "Episode 1"
           :season "1"
           :show "Shetland"
           :url "http://notabenoid.com/book/38401/140024/ready"
           :version ""}
          {:episode "2"
           :lang "russian"
           :name "Episode 2"
           :season "1"
           :show "Shetland"
           :url "http://notabenoid.com/book/38401/140260/ready"
           :version ""}
          {:episode "1"
           :lang "russian"
           :name "Raven Black (Part 1) (HDTV x264-TLA)"
           :season "2"
           :show "Shetland"
           :url "http://notabenoid.com/book/38401/204663/ready"
           :version ""}
          {:episode "2"
           :lang "russian"
           :name "Part 2 (720p.HDTV-MOS)"
           :season "2"
           :show "Shetland"
           :url "http://notabenoid.com/book/38401/206073/ready"
           :version ""}
          {:episode "3"
           :lang "russian"
           :name "Dead Water (Part 1)"
           :season "2"
           :show "Shetland"
           :url "http://notabenoid.com/book/38401/207603/ready"
           :version ""}
          {:episode "4"
           :lang "russian"
           :name "Part 2 (RiVER)"
           :season "2"
           :show "Shetland"
           :url "http://notabenoid.com/book/38401/209212/ready"
           :version ""}
          {:episode "5"
           :lang "russian"
           :name "Part 1 (RIVER)"
           :season "2"
           :show "Shetland"
           :url "http://notabenoid.com/book/38401/210731/ready"
           :version ""}]))
