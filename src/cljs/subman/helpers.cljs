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

(defn add-0-if-need
  "Add 0 before number if need"
  [number]
  (if (= (count (str number)) 1)
    (str "0" number)
    number))

(defn format-season-episode
  "Format episode numbers"
  [season episode]
  (str (when (is-filled? season)
         (str "S" (add-0-if-need season)))
       (when (is-filled? episode)
         (str "E" (add-0-if-need episode)))))
