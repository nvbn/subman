(ns subman.parser.sources.podnapisi-test
  (:require [clojure.test :refer [deftest testing is]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is=]]
            [subman.parser.sources.podnapisi :as podnapisi]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]
            [subman.const :as const]))

(def line-content
  (get-from-file "resources/fixtures/podnapisi_line.html"))

(def release-file-name "resources/fixtures/podnapisi_release.html")

(def release-html (slurp release-file-name))

(def release-content (get-from-file release-file-name))

(deftest test-make-url
  (is= (#'podnapisi/make-url "/test") "http://www.podnapisi.net/test"))

(deftest test-season-episode-part
  (testing "when can"
    (is= "6" (#'podnapisi/season-episode-part line-content 2)))
  (testing "when can't"
    (is= "" (#'podnapisi/season-episode-part (get-from-line "<td></td>") 2))))

(deftest test-get-show
  (is= "test" (#'podnapisi/get-show (-> (get-from-line "<b> test </b>")
                                        (html/select [:b])
                                        first))))

(deftest test-get-url
  (is= "http://www.podnapisi.net/test"
       (#'podnapisi/get-url (-> (get-from-line "<a href='/test'></a>")
                                (html/select [:a])
                                first))))

(deftest test-get-version
  (is= (#'podnapisi/get-version line-content)
       "Bitten.S01E06.HDTV.x264-2HDBitten.S01E06.HDTV.Xv..."))

(deftest test-get-lang
  (is= "English" (#'podnapisi/get-lang line-content)))

(deftest test-create-subtitle-map
  (is= (#'podnapisi/create-subtitle-map line-content)
       {:episode "6"
        :lang "English"
        :name ""
        :season "1"
        :show "Bitten"
        :url "http://www.podnapisi.net/ru/bitten-2014-subtitles-p2920797"
        :version "Bitten.S01E06.HDTV.x264-2HDBitten.S01E06.HDTV.Xv..."}))

(deftest test-parse-list-page
  (is= "Lab Rats" (:show (first (#'podnapisi/parse-list-page release-html)))))

(deftest test-get-release-page-url
  (is= (#'podnapisi/get-release-page-url 1)
       "http://www.podnapisi.net/en/ppodnapisi/search/sJ/-1/sS/time/sO/desc/sT/-1/sM/0/sA/0/sK//sOA/0/sOT/0/sOL/0/sOI/0/sOE/0/sOD/0/sOH/0/sY//sOCS/0/sFT/0/sR//sTS//sTE//sAKA/1/sH//sI//tbsl/1/asdp/0/page//page/1"))

(deftest test-get-htmls-for-parse
  (with-redefs [helpers/download (constantly release-html)]
    (is= (podnapisi/get-htmls-for-parse 1)
         [{:content release-html
           :url "http://www.podnapisi.net/en/ppodnapisi/search/sJ/-1/sS/time/sO/desc/sT/-1/sM/0/sA/0/sK//sOA/0/sOT/0/sOL/0/sOI/0/sOE/0/sOD/0/sOH/0/sY//sOCS/0/sFT/0/sR//sTS//sTE//sAKA/1/sH//sI//tbsl/1/asdp/0/page//page/1"}])))

(deftest test-get-subtitles
  (is= (podnapisi/get-subtitles release-html "")
       [{:episode "1"
         :lang "English"
         :name ""
         :season "1"
         :show "Lab Rats"
         :url "http://www.podnapisi.net/en/lab-rats-2008-subtitles-p2920844"
         :version "Lab.Rats.(US).-.03x01.-.Sink.or.Swim.TVVersion"}
        {:episode "6"
         :lang "English"
         :name ""
         :season "1"
         :show "Bitten"
         :url "http://www.podnapisi.net/en/bitten-2014-subtitles-p2920797"
         :version "Bitten.S01E06.HDTV.x264-2HDBitten.S01E06.HDTV.Xv..."}
        {:episode "1"
         :lang "English"
         :name ""
         :season "2"
         :show "My Mad Fat Diary"
         :url "http://www.podnapisi.net/en/my-mad-fat-diary-2012-subtitles-p2920653"
         :version nil}
        {:episode "10"
         :lang "English"
         :name ""
         :season "2"
         :show "Teenage Mutant Ninja Tu..."
         :url "http://www.podnapisi.net/en/teenage-mutant-ninja-turtles-2012-subtitles-p2920655"
         :version nil}
        {:episode "12"
         :lang "English"
         :name ""
         :season "3"
         :show "The Walking Dead"
         :url "http://www.podnapisi.net/en/the-walking-dead-2010-subtitles-p2920593"
         :version nil}
        {:episode "12"
         :lang "English"
         :name ""
         :season "3"
         :show "The Walking Dead"
         :url "http://www.podnapisi.net/en/the-walking-dead-2010-subtitles-p2920594"
         :version nil}
        {:episode "10"
         :lang "English"
         :name ""
         :season "2"
         :show "Teenage Mutant Ninja Tu..."
         :url "http://www.podnapisi.net/en/teenage-mutant-ninja-turtles-2012-subtitles-p2920523"
         :version nil}
        {:episode "10"
         :lang "English"
         :name ""
         :season "2"
         :show "Teenage Mutant Ninja Tu..."
         :url "http://www.podnapisi.net/en/teenage-mutant-ninja-turtles-2012-subtitles-p2920562"
         :version "Teenage.Mutant.Ninja.Turtles.(2012).-.02x10.-.Fun..."}
        {:episode "10"
         :lang "English"
         :name ""
         :season "2"
         :show "Teenage Mutant Ninja Tu..."
         :url "http://www.podnapisi.net/en/teenage-mutant-ninja-turtles-2012-subtitles-p2920571"
         :version nil}
        {:episode "7"
         :lang "English"
         :name ""
         :season "1"
         :show "Intelligence"
         :url "http://www.podnapisi.net/en/intelligence-2014-subtitles-p2920412"
         :version nil}
        {:episode "11"
         :lang "English"
         :name ""
         :season "2"
         :show "House of Cards"
         :url "http://www.podnapisi.net/en/house-of-cards-2013-subtitles-p2919825"
         :version "House.of.Cards.2013.S02E11.WEBRip.x264-2HD"}
        {:episode "16"
         :lang "English"
         :name ""
         :season "1"
         :show "The Fosters"
         :url "http://www.podnapisi.net/en/the-fosters-2013-subtitles-p2920411"
         :version "The.Fosters.2013.S01E16.HDTV.x264-EXCELLENCEThe...."}
        {:episode "16"
         :lang "English"
         :name ""
         :season "1"
         :show "The Fosters"
         :url "http://www.podnapisi.net/en/the-fosters-2013-subtitles-p2920409"
         :version "The.Fosters.2013.S01E16.HDTV.x264-EXCELLENCEThe...."}
        {:episode "5"
         :lang "English"
         :name ""
         :season "2"
         :show "The Following"
         :url "http://www.podnapisi.net/en/the-following-2013-subtitles-p2920408"
         :version "The.Following.S02E05.HDTV.x264-LOLThe.Following...."}
        {:episode "6"
         :lang "English"
         :name ""
         :season "3"
         :show "Switched at Birth"
         :url "http://www.podnapisi.net/en/switched-at-birth-2011-subtitles-p2920378"
         :version nil}
        {:episode "6"
         :lang "English"
         :name ""
         :season "3"
         :show "Switched at Birth"
         :url "http://www.podnapisi.net/en/switched-at-birth-2011-subtitles-p2920354"
         :version nil}
        {:episode ""
         :lang "English"
         :name ""
         :season ""
         :show "Čuvar plaže u zimskom periodu"
         :url "http://www.podnapisi.net/en/uvar-plaze-u-zimskom-periodu-1976-subtitles-p2917672"
         :version nil}
        {:episode "10"
         :lang "English"
         :name ""
         :season "1"
         :show "Bates Motel"
         :url "http://www.podnapisi.net/en/bates-motel-2013-subtitles-p2920123"
         :version "Bates.Motel.S01E10.Midnight.720p.BluRay.x264-DEMAND"}
        {:episode ""
         :lang "English"
         :name ""
         :season ""
         :show "Beastmaster, The"
         :url "http://www.podnapisi.net/en/beastmaster-the-1982-subtitles-p2920089"
         :version nil}
        {:episode "1"
         :lang "English"
         :name ""
         :season "5"
         :show "Star Wars: The Clone Wars"
         :url "http://www.podnapisi.net/en/star-wars-the-clone-wars-2008-subtitles-p2919969"
         :version nil}
        {:episode "4"
         :lang "English"
         :name ""
         :season "53"
         :show "Horizon"
         :url "http://www.podnapisi.net/en/horizon-1964-subtitles-p2919927"
         :version nil}
        {:episode ""
         :lang "English"
         :name ""
         :season ""
         :show "Brève traversée"
         :url "http://www.podnapisi.net/en/br-ve-travers-e-2001-subtitles-p2919915"
         :version "Brief.Crossing.2001.PAL.DVD.x264-Tree"}
        {:episode ""
         :lang "English"
         :name ""
         :season ""
         :show "Magnificent Ambersons, The"
         :url "http://www.podnapisi.net/en/magnificent-ambersons-the-2002-subtitles-p2919913"
         :version "The.Magnificent.Ambersons.2002.DVDRip.Xvid"}
        {:episode "1"
         :lang "English"
         :name ""
         :season "3"
         :show "Sherlock"
         :url "http://www.podnapisi.net/en/sherlock-2010-subtitles-p2919896"
         :version "Sherlock.S03E01.720p.WEB-DL.DD5.1.H.264-BSSherlo..."}
        {:episode "1"
         :lang "English"
         :name ""
         :season "3"
         :show "Sherlock"
         :url "http://www.podnapisi.net/en/sherlock-2010-subtitles-p2919899"
         :version "Sherlock.S03E01.720p.WEB-DL.DD5.1.H.264-BSSherlo..."}
        {:episode ""
         :lang "English"
         :name ""
         :season ""
         :show "Anatomie de l'enfer"
         :url "http://www.podnapisi.net/en/anatomie-de-l-enfer-2004-subtitles-p2919664"
         :version "Anatomy.of.Hell.2004.NTSC.DVD.x264-Tree"}
        {:episode "9"
         :lang "English"
         :name ""
         :season "1"
         :show "Bates Motel"
         :url "http://www.podnapisi.net/en/bates-motel-2013-subtitles-p2919655"
         :version "Bates.Motel.S01E09.Underwater.720p.BluRay.x264-DE..."}
        {:episode "9"
         :lang "English"
         :name ""
         :season "1"
         :show "Bates Motel"
         :url "http://www.podnapisi.net/en/bates-motel-2013-subtitles-p2919642"
         :version nil}
        {:episode "9"
         :lang "English"
         :name ""
         :season "1"
         :show "Bates Motel"
         :url "http://www.podnapisi.net/en/bates-motel-2013-subtitles-p2919644"
         :version "Bates.Motel.S01E09.HDTV.XviD.FUMBates.Motel.S01E..."}
        {:episode "11"
         :lang "English"
         :name ""
         :season "1"
         :show "Almost Human"
         :url "http://www.podnapisi.net/en/almost-human-2013-subtitles-p2919537"
         :version "Almost.Human.S01E11.HDTV.x264-LOL"}]))
