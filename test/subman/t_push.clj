(ns subman.t-push
  (:require [midje.sweet :refer [fact => provided with-state-changes
                                 before after truthy]]
            [org.httpkit.server :as server]
            [subman.push :as push]
            [subman.models :as models]))

(deftype FakeChannel [calls]
  server/Channel
  (send! [_ _] (swap! calls inc)))

(let [orig-subs (atom #{})
      orig-count (atom 0)
      sub (atom 0)]
  (with-state-changes [(before :facts (reset! push/subscribers @orig-subs))
                       (before :facts (reset! models/total-count @orig-count))
                       (before :facts (swap! push/subscribers conj (FakeChannel. sub)))
                       (after :facts (reset! orig-subs @push/subscribers))
                       (after :facts (reset! orig-count @models/total-count))
                       (after :facts (reset! sub 0))]
    (fact "should notify all subscribers"
          (do (swap! models/total-count inc)
            @sub) => 1)))
