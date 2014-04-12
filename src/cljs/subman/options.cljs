(ns subman.options
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [cljs.reader :refer [read-string]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [goog.storage.mechanism.HTML5LocalStorage]
            [subman.const :as const]))

(defn get-local-storage
  "Get local storage instance"
  []
  (goog.storage.mechanism.HTML5LocalStorage.))

(defn get-languages
  "Get atom with all languages list"
  []
  (let [languages (atom [])]
    (go (let [response (<! (http/get "/api/list-languages/"))]
          (->> (:body response)
               read-string
               (map #(:term %))
               (reset! languages))))
    languages))

(defn get-sources
  "Get all sources list"
  []
  (atom (map string/lower-case (vals const/type-names))))

(defn get-from-storage
  "Get from storage or val"
  [storage name default]
  (let [current (try (.get storage name)
                  (catch js/Error e default))]
    (if (nil? current)
      default
      current)))

(defn get-option
  "Get option value atom"
  [storage name default]
  (let [option (atom (get-from-storage storage name default))]
    (add-watch option :options
               (fn [_ _ _ value]
                 (.set storage name value)))
    option))

(defn get-language-option
  "Get current language option"
  [storage]
  (get-option storage "language" const/default-language))

(defn get-source-option
  "Get current language option"
  [storage]
  (get-option storage "source" (get const/type-names
                                    const/default-type)))
