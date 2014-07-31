(ns subman.core-test
  (:require [clojure.test :refer [is]]))

(defn is-do
  "Equal to (is (fnc body))"
  [fnc & body]
  (is (apply fnc body)))

(defn is=
  [& body]
  (apply is-do = body))

(defmacro with-provided
  [redefs & body]
  `(with-redefs-fn ~redefs
     (fn [] ~@body)))
