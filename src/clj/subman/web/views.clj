(ns subman.web.views
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [environ.core :refer [env]]
            [subman.helpers :refer [as-static make-static]]))

(defn index-page []
  (let [is-debug (env :is-debug)]
    (html5 [:head
            [:link {:rel  "icon"
                    :type "image/png"
                    :href (first (make-static "favicon.png"))}]
            [:link {:type "application/opensearchdescription+xml"
                    :rel  "search"
                    :href (first (make-static "opensearch.xml"))}]
            [:title "Subman - subtitle search service"]
            (when-not is-debug
              (as-static include-css
                         "components/bootstrap/dist/css/bootstrap.min.css"
                         "components/bootstrap/dist/css/bootstrap-theme.min.css"
                         "components/font-awesome/css/font-awesome.min.css"
                         "components/typeahead.js-bootstrap3.less/typeahead.css"
                         "main.css"))
            (when-not is-debug
              (as-static include-js
                         "components/jquery/dist/jquery.min.js"
                         "components/bootstrap/dist/js/bootstrap.min.js"
                         "components/typeahead.js/dist/typeahead.jquery.min.js"
                         "components/react/react.min.js"
                         "main.js"))
            (when is-debug
              (as-static include-css
                         "components/bootstrap/dist/css/bootstrap.css"
                         "components/bootstrap/dist/css/bootstrap-theme.css"
                         "components/font-awesome/css/font-awesome.css"
                         "components/typeahead.js-bootstrap3.less/typeahead.css"
                         "main.css"))
            (when is-debug
              (as-static include-js
                         "components/jquery/dist/jquery.js"
                         "components/bootstrap/dist/js/bootstrap.js"
                         "components/typeahead.js/dist/typeahead.jquery.js"
                         "components/react/react.js"
                         "cljs-target/goog/base.js"
                         "main.js"))
            [:script "goog.require('subman.core');"]]
           [:body [:script "subman.core.run();"]])))
