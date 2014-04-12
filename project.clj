(defproject subman "0.1.0-SNAPSHOT"
  :description "service for fast searching subtitles"
  :url "https://github.com/nvbn/subman"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [enlive "1.1.5"]
                 [clojurewerkz/elastisch "1.5.0-beta3"]
                 [reagent "0.4.2"]
                 [cljs-http "0.1.9"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [overtone/at-at "1.2.0"]
                 [garden "1.1.5"]
                 [org.clojure/data.json "0.2.4"]
                 [http-kit "2.1.18"]
                 [ring "1.2.2"]
                 [swiss-arrows "1.0.0"]
                 [im.chit/purnam.test "0.4.3"]
                 [jayq "2.5.0"]]
  :plugins [[lein-cljsbuild "1.0.3"]
            [com.keminglabs/cljx "0.3.2"]
            [lein-garden "0.1.8"]]
  :main subman.core
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]
                   :cljsbuild {:builds
                               {:main {:source-paths ["src/cljs" "target/generated-cljs"]
                                       :compiler {:preamble ["reagent/react.js"]
                                                  :output-to "resources/public/main.js"
                                                  :pretty-print true
                                                  :optimizations :simple
                                                  :output-dir "resources/public/cljs-target"}}
                                :test {:source-paths ["src/cljs" "test/cljs"
                                                      "target/generated-cljs"]
                                       :compiler {:preamble ["reagent/react.js"]
                                                  :output-to "target/karma-test.js"
                                                  :optimizations :simple
                                                  :pretty-print true}}}}}
             :uberjar {:aot :all
                       :cljsbuild {:builds [{:source-paths ["src/cljs" "target/generated-cljs"]
                                             :compiler {:preamble ["reagent/react.min.js"]
                                                        :externs ["resources/public/components/jquery/dist/jquery.min.js"
                                                                  "resources/public/components/bootstrap/dist/js/bootstrap.min.js"
                                                                  "resources/public/components/typeahead.js/dist/typeahead.jquery.min.js"]
                                                        :output-to "resources/public/main.js"
                                                        :optimizations :advanced
                                                        :pretty-print false}
                                             :jar true}]}
                       :hooks [cljx.hooks
                               leiningen.cljsbuild]}}
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path "target/classes"
                   :rules :clj}
                  {:source-paths ["src/cljx"]
                   :output-path "target/generated-cljs"
                   :rules :cljs}]}
  :garden {:builds [{:stylesheet subman.web.style/main
                     :compiler {:output-to "resources/public/main.css"}}]})
