(ns subman.parser.sources.opensubtitles-test
  (:require [clojure.test :refer [deftest testing is]]
            [net.cgrand.enlive-html :as html]
            [test-sugar.core :refer [is= is-do]]
            [subman.parser.sources.opensubtitles :as opensubtitles]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]
            [subman.const :as const]))

(def line-content
  (get-from-file "resources/fixtures/opensubtitles_line.html"))

(def release-filename "resources/fixtures/opensubtitles_release.html")

(def release-html (slurp release-filename))

(def release-content (get-from-file release-filename))

(def titles-td (first (html/select line-content [:td])))

(def main-link (first (html/select line-content [:td :strong :a])))

(deftest test-make-url
  (is= (#'opensubtitles/make-url "/test")
       "http://www.opensubtitles.org/test"))

(deftest test-get-page-url
  (is= (#'opensubtitles/get-page-url 2)
       "http://www.opensubtitles.org/en/search/sublanguageid-all/offset-40"))

(deftest test-get-from-season-part
  (testing "when can get"
    (is= (#'opensubtitles/get-from-season-part #"S(\d+)" "S01")
         "1"))
  (testing "when can't"
    (is= (#'opensubtitles/get-from-season-part #"S(\d+)" "123")
         "")))

(deftest test-remove-brs
  (is= (#'opensubtitles/remove-brs "t<br>es<br />t")
       "t es t"))

(deftest test-get-from-show-part
  (testing "when can get"
    (is= (#'opensubtitles/get-from-show-part #"\"(.+)\"" "\"test\"")
         "test"))
  (testing "when can't"
    (is= (#'opensubtitles/get-from-show-part #"\"(.+)\"" "test")
         ""))
  (testing "when can't with default"
    (is= (#'opensubtitles/get-from-show-part #"\"(.+)\"" "test" "1")
         "1")))

(deftest test-get-seasons-part
  (is= (#'opensubtitles/get-seasons-part titles-td)
       "\n\t\t[S01E17]\n\t\tDads (2013) - 01x17 - Enemies of Bill.EXCELLENCE"))

(deftest test-get-show-part
  (is= (#'opensubtitles/get-show-part main-link)
       "\"Dads\" Enemies of Bill\n \t\t\t(2014)"))

(deftest test-get-url
  (is= (#'opensubtitles/get-url main-link)
       "http://www.opensubtitles.org/en/subtitles/5547771/dads-enemies-of-bill-en"))

(deftest test-get-version
  (is= (#'opensubtitles/get-version titles-td) ""))

(deftest test-create-subtitle
  (is= (#'opensubtitles/create-subtitle (-> line-content
                                            (html/select [:tr])
                                            first))
       {:episode "17"
        :lang "English"
        :name "Enemies of Bill"
        :season "1"
        :show "Dads"
        :url "http://www.opensubtitles.org/en/subtitles/5547771/dads-enemies-of-bill-en"
        :version ""}))

(deftest test-get-htmls-for-parse
  (with-redefs [helpers/download (constantly release-html)]
    (is= (opensubtitles/get-htmls-for-parse 1)
         [{:content release-html
           :url "http://www.opensubtitles.org/en/search/sublanguageid-all/offset-0"}])))

(deftest test-get-subtitles
  (is= (opensubtitles/get-subtitles release-html "")
       [{:episode "7"
         :lang "Hebrew"
         :name "Studies in Modern Movement"
         :season "3"
         :show "Community"
         :url "http://www.opensubtitles.org/en/subtitles/5547811/community-studies-in-modern-movement-he"
         :version ""}
        {:episode ""
         :lang "English"
         :name ""
         :season ""
         :show "Sader Ridge (2013)"
         :url "http://www.opensubtitles.org/en/subtitles/5547810/sader-ridge-en"
         :version ""}
        {:episode ""
         :lang "Portuguese"
         :name "Bug"
         :season ""
         :show "Breaking Bad"
         :url "http://www.opensubtitles.org/en/subtitles/5547809/breaking-bad-bug-pt"
         :version ""}
        {:episode ""
         :lang "Italian"
         :name "Looking for the Future"
         :season ""
         :show "Looking"
         :url "http://www.opensubtitles.org/en/subtitles/5547808/looking-looking-for-the-future-it"
         :version ""}
        {:episode ""
         :lang "Turkish"
         :name "Disrupt"
         :season ""
         :show "Almost Human"
         :url "http://www.opensubtitles.org/en/subtitles/5547807/almost-human-disrupt-tr"
         :version ""}
        {:episode ""
         :lang "Italian"
         :name "Looking for the Future"
         :season ""
         :show "Looking"
         :url "http://www.opensubtitles.org/en/subtitles/5547806/looking-looking-for-the-future-it"
         :version ""}
        {:episode ""
         :lang "Portuguese"
         :name "Redemption"
         :season ""
         :show "Beauty and the Beast"
         :url "http://www.opensubtitles.org/en/subtitles/5547805/beauty-and-the-beast-redemption-pt"
         :version ""}
        {:episode ""
         :lang "Estonian"
         :name ""
         :season ""
         :show "You're Next (2011)"
         :url "http://www.opensubtitles.org/en/subtitles/5547804/you-re-next-et"
         :version ""}
        {:episode ""
         :lang "Italian"
         :name "The Scream"
         :season ""
         :show "Switched at Birth"
         :url "http://www.opensubtitles.org/en/subtitles/5547803/switched-at-birth-the-scream-it"
         :version ""}
        {:episode "17"
         :lang "Serbian"
         :name "Workforce: Part 2"
         :season "7"
         :show "Star Trek: Voyager"
         :url "http://www.opensubtitles.org/en/subtitles/5547802/star-trek-voyager-workforce-part-2-sr"
         :version ""}
        {:episode ""
         :lang "Italian"
         :name "The Scream"
         :season ""
         :show "Switched at Birth"
         :url "http://www.opensubtitles.org/en/subtitles/5547801/switched-at-birth-the-scream-it"
         :version ""}
        {:episode "5"
         :lang "Turkish"
         :name "Reflection"
         :season "2"
         :show "The Following"
         :url "http://www.opensubtitles.org/en/subtitles/5547800/the-following-reflection-tr"
         :version ""}
        {:episode ""
         :lang "Polish"
         :name ""
         :season ""
         :show "Flowers in the Attic (2014)"
         :url "http://www.opensubtitles.org/en/subtitles/5547799/flowers-in-the-attic-pl"
         :version ""}
        {:episode ""
         :lang "Hebrew"
         :name "Patient Zero"
         :season ""
         :show "Intelligence"
         :url "http://www.opensubtitles.org/en/subtitles/5547798/intelligence-patient-zero-he"
         :version ""}
        {:episode "10"
         :lang "Croatian"
         :name "In Excelsis Deo"
         :season "1"
         :show "The West Wing"
         :url "http://www.opensubtitles.org/en/subtitles/5547797/the-west-wing-in-excelsis-deo-hr"
         :version ""}
        {:episode ""
         :lang "Polish"
         :name ""
         :season ""
         :show "Training Day (2001)"
         :url "http://www.opensubtitles.org/en/subtitles/5547796/training-day-pl"
         :version ""}
        {:episode ""
         :lang "Greek"
         :name ""
         :season ""
         :show "Frankenstein Must Be Destroyed (1969)"
         :url "http://www.opensubtitles.org/en/subtitles/5547795/frankenstein-must-be-destroyed-el"
         :version ""}
        {:episode ""
         :lang "Bulgarian"
         :name ""
         :season ""
         :show "In the Name of the King III (2014)"
         :url "http://www.opensubtitles.org/en/subtitles/5547794/in-the-name-of-the-king-iii-bg"
         :version "In.the.Name.of.the.King.III.2014.1080p.BluRay.x... "}
        {:episode ""
         :lang "Portuguese-BR"
         :name ""
         :season ""
         :show "The Family (2013)"
         :url "http://www.opensubtitles.org/en/subtitles/5547793/the-family-pb"
         :version ""}
        {:episode ""
         :lang "Polish"
         :name "The Wheaton Recurrence"
         :season ""
         :show "The Big Bang Theory"
         :url "http://www.opensubtitles.org/en/subtitles/5547792/the-big-bang-theory-the-wheaton-recurrence-pl"
         :version ""}
        {:episode ""
         :lang "English"
         :name ""
         :season ""
         :show "The Sweeney (2012)"
         :url "http://www.opensubtitles.org/en/subtitles/5547791/the-sweeney-en"
         :version ""}
        {:episode ""
         :lang "Czech"
         :name "Episode #2.6"
         :season ""
         :show "House of Cards"
         :url "http://www.opensubtitles.org/en/subtitles/5547790/house-of-cards-episode-2-6-cs"
         :version ""}
        {:episode ""
         :lang "Polish"
         :name ""
         :season ""
         :show "12 Years a Slave (2013)"
         :url "http://www.opensubtitles.org/en/subtitles/5547789/12-years-a-slave-pl"
         :version ""}
        {:episode ""
         :lang "Norwegian"
         :name "Crucifixed"
         :season ""
         :show "Sons of Anarchy"
         :url "http://www.opensubtitles.org/en/subtitles/5547788/sons-of-anarchy-crucifixed-no"
         :version "Sons.of.Anarchy.S05E10.Retail.DKsubs.720p.BluRa... "}
        {:episode "20"
         :lang "English"
         :name "Shadow Walker"
         :season "2"
         :show "Nikita"
         :url "http://www.opensubtitles.org/en/subtitles/5547786/nikita-shadow-walker-en"
         :version ""}
        {:episode "12"
         :lang "English"
         :name "Devil's Planet"
         :season "3"
         :show "Doctor Who"
         :url "http://www.opensubtitles.org/en/subtitles/5547785/doctor-who-devil-s-planet-en"
         :version ""}
        {:episode ""
         :lang "Arabic"
         :name "Perception"
         :season ""
         :show "Almost Human"
         :url "http://www.opensubtitles.org/en/subtitles/5547784/almost-human-perception-ar"
         :version ""}
        {:episode ""
         :lang "Korean"
         :name ""
         :season ""
         :show "RocknRolla (2008)"
         :url "http://www.opensubtitles.org/en/subtitles/5547783/rocknrolla-ko"
         :version ""}
        {:episode ""
         :lang "Czech"
         :name "Episode #2.6"
         :season ""
         :show "House of Cards"
         :url "http://www.opensubtitles.org/en/subtitles/5547782/house-of-cards-episode-2-6-cs"
         :version "House.Of.Cards.2013.S02E06.720p.WEB-DL.x264-Soh... "}
        {:episode ""
         :lang "Dutch"
         :name "In and Out"
         :season ""
         :show "Killer Women"
         :url "http://www.opensubtitles.org/en/subtitles/5547781/killer-women-in-and-out-nl"
         :version ""}
        {:episode "11"
         :lang "English"
         :name "Day of Armageddon"
         :season "3"
         :show "Doctor Who"
         :url "http://www.opensubtitles.org/en/subtitles/5547780/doctor-who-day-of-armageddon-en"
         :version ""}
        {:episode "1"
         :lang "Spanish"
         :name "The Train Job"
         :season "1"
         :show "Firefly"
         :url "http://www.opensubtitles.org/en/subtitles/5547779/firefly-the-train-job-es"
         :version ""}
        {:episode ""
         :lang "English"
         :name "Size Matters"
         :season ""
         :show "Intelligence"
         :url "http://www.opensubtitles.org/en/subtitles/5547778/intelligence-size-matters-en"
         :version ""}
        {:episode ""
         :lang "Czech"
         :name ""
         :season ""
         :show "12 Years a Slave (2013)"
         :url "http://www.opensubtitles.org/en/subtitles/5547777/12-years-a-slave-cs"
         :version ""}
        {:episode "18"
         :lang "Portuguese-BR"
         :name "Wash"
         :season "2"
         :show "Prison Break"
         :url "http://www.opensubtitles.org/en/subtitles/5547776/prison-break-wash-pb"
         :version ""}
        {:episode ""
         :lang "Hungarian"
         :name "Looking for the Future"
         :season ""
         :show "Looking"
         :url "http://www.opensubtitles.org/en/subtitles/5547775/looking-looking-for-the-future-hu"
         :version ""}
        {:episode ""
         :lang "Serbian"
         :name ""
         :season ""
         :show "Heli (2013)"
         :url "http://www.opensubtitles.org/en/subtitles/5547774/heli-sr"
         :version ""}
        {:episode ""
         :lang "Arabic"
         :name "Letharia Vulpira"
         :season ""
         :show "Teen Wolf"
         :url "http://www.opensubtitles.org/en/subtitles/5547773/teen-wolf-letharia-vulpira-ar"
         :version ""}
        {:episode ""
         :lang "Serbian"
         :name ""
         :season ""
         :show "Heli (2013)"
         :url "http://www.opensubtitles.org/en/subtitles/5547772/heli-sr"
         :version ""}
        {:episode ""
         :lang "English"
         :name "Enemies of Bill"
         :season ""
         :show "Dads"
         :url "http://www.opensubtitles.org/en/subtitles/5547771/dads-enemies-of-bill-en"
         :version ""}]))
