(ns subman.locks
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [timeout <!]]))

; Locks for preventing runing async tests concurently

(def http-locks (atom []))

(defn take-http!
  []
  (let [lock (gensym)]
    (swap! http-locks conj lock)
    (go-loop []
             (when (not= lock (first @http-locks))
               (<! (timeout 100))
               (recur))
             (println "lock"))))

(defn free-http!
  []
  (swap! http-locks rest)
  (println "free"))
