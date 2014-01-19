(ns subman.helpers)

(defn print-first [arg fnc & args]
  (println arg)
  (apply fnc arg args))
