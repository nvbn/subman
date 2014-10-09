(ns subman.routes-test
  (:require [cemerick.cljs.test :refer-macros [deftest testing]]
            [test-sugar.core :refer [is=]]
            [clj-di.core :refer [register! get-dep]]
            [secretary.core :as s]
            [subman.helpers :refer [DummyHistory]]
            [subman.routes :as r]))

(deftest test-set-search-query
  (let [state (atom {})]
    (s/set-config! :state state)
    (r/set-search-query "test")
    (is= (:stable-search-query @state)
         "test")))

(deftest test-main-page
  (is= "/" (r/main-page)))

(deftest test-search-page
  (is= "/search/Family%20Guy%20S12E08"
       (r/search-page {:query "Family Guy S12E08"})))

(deftest test-change-url!
  (register! :history (DummyHistory. ""))
  (r/change-url! "/test/url" "test-url")
  (is= (.-token (get-dep :history)) "/test/url")
  (is= (.-title js/document) "test-url"))
