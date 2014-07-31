(ns subman.web.routes-test
  (:require [clojure.test :refer [deftest is]]
            [subman.web.routes :as routes]))

(deftest test-main-routes
  (is routes/main-routes))
