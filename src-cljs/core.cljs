(ns subman.core
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:use-macros [purnam.js :only [obj arr !]]
               [purnam.angular :only [def.module
                                      def.controller
                                      def.service]]))

(def.module subman [])

(defn- success [promise fnc] (promise/success fnc))

(def.controller subman.Search [$scope $http]
  (! $scope.updateFilter (fn [] (-> (str "/api/search/?query=" $scope.query)
                                    $http/get
                                    (success #(! $scope.results %))))))
