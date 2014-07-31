(ns subman.web.style-test
  (:require [clojure.test :refer [deftest is]]
            [subman.web.style :as style]))

(deftest test-main-style
  (is style/main))
