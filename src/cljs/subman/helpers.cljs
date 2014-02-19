(ns subman.helpers)

(defn is-filled?
  "Is field filled"
  [value]
  (and (not (nil? value))
       (not= "" value)))

(defn truthy
  "If x is truthy"
  [x]
  (and (not= "" x)
       (not= [] x)
       (not= nil x)
       (not= false x)))
