(ns subman.sources.t-addicted
  (:require [midje.sweet :refer [facts fact anything =>]]
            [net.cgrand.enlive-html :as html]
            [subman.sources.addicted :as addicted]
            [subman.helpers :as helpers :refer [get-from-file get-from-line]]))

(defn get-single-episode
  "Get parsed html for single episode"
  [] (get-from-file "test/subman/sources/fixtures/addicted_episode.html"))

(facts "should make correct url"
       (fact "when start with /"
             (#'addicted/make-url "/test/") => "http://www.addic7ed.com/test/")
       (fact "when not"
             (#'addicted/make-url "test/") => "http://www.addic7ed.com/test/"))

(facts "check for version line"
       (fact "when version line passed"
             (#'addicted/is-version-line?
              (get-from-line
               "<td class='NewsTitle'><img src='/images/folder_page.png' /></td>"))
             => true)
       (fact "when not"
             (#'addicted/is-version-line? (get-from-line "<td></td>")) => nil))

(facts "check for language line"
       (fact "when language line passed"
             (#'addicted/is-language-line?
              (get-from-line
               "<td class='language'><a href='#'><b></b></a></td>")) => true)
       (fact "when not"
             (#'addicted/is-language-line?
              (get-from-line "<td></td>")) => false))

(facts "get version"
       (fact "when has name"
             (#'addicted/get-version (get-from-line
                                      "<td class='NewsTitle'>
                                        <b><span></span>test</b>
                                      </td>")) => {:name "test"
                                                   :langs []})
       (fact "when not"
             (#'addicted/get-version (get-from-line
                                      "<td class='NewsTitle'>
                                        <b><span></span></b>
                                      </td>")) => {:name ""
                                                   :langs []}))

(fact "get language"
      (#'addicted/get-lang (get-from-line
                            "<td class='language'><span>test</span></td>
                             <td>
                              <a href='test-url' class='buttonDownload'></a>
                             </td>")) => {:name "test"
                                          :url "http://www.addic7ed.com/test-url"})

(fact "adding lang to version"
      (#'addicted/add-lang "" {:langs []}) => {:langs ["test"]}
      (provided (#'addicted/get-lang anything) => "test"))

(fact "get subtitles from lines"
      (#'addicted/get-subtitles [1 2]) => [{:name "test"
                                            :langs ["us"]}]
      (provided (#'addicted/is-version-line? 1) => true
                (#'addicted/get-version 1) => {:name "test"
                                               :langs []}
                (#'addicted/is-version-line? 2) => false
                (#'addicted/is-language-line? 2) => true
                (#'addicted/add-lang 2 anything) => {:name "test"
                                                     :langs ["us"]}))

(facts "get versions should"
       (fact "return all versions"
             (count (#'addicted/get-versions {:url ""})) => 2
             (provided (helpers/fetch anything) => (get-single-episode)))
       (fact "return all languages"
             (-> (#'addicted/get-versions {:url ""})
                 first
                 :langs
                 count) => 3
             (provided (helpers/fetch anything) => (get-single-episode))))

(fact "get release url"
      (#'addicted/get-releases-url 1)
      =>"http://www.addic7ed.com/log.php?mode=versions&page=1")

(fact "get episode from release line"
      (#'addicted/episode-from-release
       (-> (get-from-file "test/subman/sources/fixtures/addicted_line.html")
           (html/select [:a])
           first)) => {:episode "6"
                       :name "Episode Six"
                       :season "3"
                       :show "Episodes"
                       :url "http://www.addic7ed.com/serie/Episodes/3/6/Episode_Six"})

(fact "get release page result"
      (-> (#'addicted/get-release-page-result 1)
          first
          :name) => "Family Affair"
      (provided
       (helpers/fetch (#'addicted/get-releases-url 1))
       => (get-from-file "test/subman/sources/fixtures/addicted_release.html")
       (helpers/fetch anything)
       => (get-from-file "test/subman/sources/fixtures/addicted_episode.html")))
