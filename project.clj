(defproject subman "0.1.0-SNAPSHOT"
  :description "service for fast searching subtitles"
  :url "https://github.com/nvbn/subman"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.4"]
                 [enlive "1.1.5"]
                 [clojurewerkz/elastisch "1.5.0-beta1"]
                 [reagent "0.3.0"]
                 [cljs-http "0.1.7"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [overtone/at-at "1.2.0"]
                 [garden "1.1.5"]]
  :ring {:handler subman.core/app}
  :plugins [[lein-ring "0.8.10"]
            [lein-cljsbuild "1.0.1"]
            [com.keminglabs/cljx "0.3.2"]
            [lein-garden "0.1.5"]]
  :profiles {:dev {:dependencies [[midje "1.6.0"]]}}
  :source-paths ["src/clj"]
  :cljsbuild {:builds [{:source-paths ["src/cljs" "target/generated-cljs"]
                        :compiler {:preamble ["reagent/react.min.js"]
                                   :output-to "resources/public/main.js"
                                   :optimizations :advanced
                                   :pretty-print false}
                        :jar true}]}
  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path "target/classes"
                   :rules :clj}
                  {:source-paths ["src/cljx"]
                   :output-path "target/generated-cljs"
                   :rules :cljs}]}
  :garden {:builds [{:stylesheet subman.style/main
                     :compiler {:output-to "resources/public/main.css"}}]}
  :hooks [cljx.hooks
          leiningen.cljsbuild])
