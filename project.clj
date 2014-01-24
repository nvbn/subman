(defproject subman "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [compojure "1.1.6"]
                 [lib-noir "0.7.9"]
                 [hiccup "1.0.4"]
                 [garden "1.1.4"]
                 [im.chit/purnam "0.1.8"]
                 [com.novemberain/monger "1.7.0-beta1"]
                 [enlive "1.1.5"]
                 [clj-http "0.7.8"]
                 [clojurewerkz/elastisch "1.5.0-beta1"]
                 [org.clojure/data.json "0.2.3"]]
  :ring {:handler subman.routes/app}
  :plugins [[lein-ring "0.8.10"]
            [lein-cljsbuild "1.0.1"]]
  :profiles {:dev {:dependencies [[midje "1.6.0"]]}}
  :cljsbuild {
              :builds [{
                        :source-paths ["src-cljs"]
                        :compiler {
                                   :output-to "resources/public/main.js"}}]})
