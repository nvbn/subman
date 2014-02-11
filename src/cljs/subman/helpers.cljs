(ns subman.helpers)

(defn is-filled?
  "Is field filled"
  [value] (and (not (nil? value))
               (not= "" value)))
