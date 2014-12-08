(defproject subman "0.1.0-SNAPSHOT"
            :description "service for fast searching subtitles"
            :url "https://github.com/nvbn/subman"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojurescript "0.0-2356"]
                           [compojure "1.2.0"]
                           [hiccup "1.0.5"]
                           [enlive "1.1.5"]
                           [clojurewerkz/elastisch "2.0.0"]
                           [cljs-http "0.1.16"]
                           [clj-http "1.0.0"]
                           [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                           [overtone/at-at "1.2.0"]
                           [garden "1.2.1"]
                           [ring "1.3.1"]
                           [swiss-arrows "1.0.0"]
                           [jayq "2.5.2"]
                           [om "0.7.3"]
                           [environ "1.0.0"]
                           [test-sugar "2.1"]
                           [alandipert/storage-atom "1.2.3"]
                           [secretary "1.2.1"]
                           [sablono "0.2.22"]
                           [prismatic/om-tools "0.3.3"]
                           [com.cognitect/transit-clj "0.8.259"]
                           [com.cognitect/transit-cljs "0.8.188"]
                           [ring-transit "0.1.2"]
                           [org.clojure/tools.logging "0.3.1"]
                           [com.cemerick/url "0.1.1"]
                           [itsy "0.1.1"]
                           [clj-di "0.1.3"]]
            :plugins [[lein-cljsbuild "1.0.3"]
                      [com.keminglabs/cljx "0.4.0"]
                      [lein-garden "0.2.1"]
                      [lein-environ "1.0.0"]
                      [lein-ring "0.8.11"]
                      [lein-ancient "0.5.5"]
                      [com.cemerick/clojurescript.test "0.3.1"]
                      [lein-bower "0.5.1"]]
            :profiles {:dev {:cljsbuild {:builds
                                         {:main {:source-paths ["src/cljs" "target/generated-cljs"]
                                                 :compiler {:output-to "resources/public/main.js"
                                                            :output-dir "resources/public/cljs-target"
                                                            :source-map true
                                                            :optimizations :none}}
                                          :test {:source-paths ["src/cljs" "test/cljs"
                                                                "target/generated-cljs"]
                                                 :compiler {:output-to "target/cljs-test.js"
                                                            :optimizations :whitespace
                                                            :pretty-print true}}}
                                         :test-commands {"test" ["phantomjs" :runner
                                                                 "resources/public/components/es5-shim/es5-shim.js"
                                                                 "resources/public/components/es5-shim/es5-sham.js"
                                                                 "resources/public/components/jquery/dist/jquery.js"
                                                                 "resources/public/components/bootstrap/dist/js/bootstrap.js"
                                                                 "resources/public/components/typeahead.js/dist/typeahead.jquery.js"
                                                                 "resources/public/components/react/react-with-addons.js"
                                                                 "target/cljs-test.js"]}}
                             :env {:is-debug true
                                   :ga-id ""
                                   :site-url "http://localhost:3000/"
                                   :db-host "http://127.0.0.1:9200"
                                   :index-name "subman7"}
                             :jvm-opts ["-Xss16m"]}
                       :production {:cljsbuild {:builds [{:source-paths ["src/cljs" "target/generated-cljs"]
                                                          :compiler {:externs ["resources/public/components/jquery/dist/jquery.min.js"
                                                                               "resources/public/components/bootstrap/dist/js/bootstrap.min.js"
                                                                               "resources/public/components/typeahead.js/dist/typeahead.jquery.min.js"
                                                                               "resources/public/components/react/react.min.js"]
                                                                     :output-to "resources/public/main.js"
                                                                     :optimizations :advanced
                                                                     :pretty-print false}}]}
                                    :env {:is-debug false
                                          :ga-id "UA-54135564-1"
                                          :site-url "http://subman.io/"
                                          :db-host "http://127.0.0.1:9200"
                                          :index-name "subman7"}}}
            :source-paths ["src/clj"]
            :test-paths ["test/clj"]
            :main subman.core
            :cljx {:builds [{:source-paths ["src/cljx"]
                             :output-path "target/classes"
                             :rules :clj}
                            {:source-paths ["src/cljx"]
                             :output-path "target/generated-cljs"
                             :rules :cljs}]}
            :garden {:builds [{:source-paths ["src/clj"]
                               :stylesheet subman.web.style/main
                               :compiler {:output-to "resources/public/main.css"}}]}
            :ring {:handler subman.handlers/app
                   :init subman.handlers/init}
            :bower {:directory "resources/public/components"}
            :bower-dependencies [["bootstrap" "3.2.0"]
                                 ["font-awesome" "4.2.0"]
                                 ["jquery" "2.1.1"]
                                 ["typeahead.js" "0.10.5"]
                                 ["typeahead.js-bootstrap3.less" "develop"]
                                 ["react" "0.11.2"]
                                 ["es5-shim" "4.0.3"]])
