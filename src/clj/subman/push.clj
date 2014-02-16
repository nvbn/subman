(ns subman.push
  (:use [org.httpkit.server :only [with-channel
                                   on-close
                                   send!]]
        [subman.models :only [total-count]]))

(def subscribers (atom #{}))

(defn notifications
  "Push notifications handler"
  [request] (with-channel request con
              (swap! subscribers conj con)
              (on-close con (fn [status]
                              (swap! subscribers disj con)))))

(add-watch total-count :notifications
           (fn [key ref old-value new-value]
             (for [con @subscribers]
               (send! con (prn-str {:total-count new-value})))))
