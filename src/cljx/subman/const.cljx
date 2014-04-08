(ns subman.const)

(def db-host "http://127.0.0.1:9200")

(def index-name "subman7")

(def type-addicted 0)

(def type-podnapisi 1)

(def type-opensubtitles 2)

(def type-subscene 3)

(def type-names {type-addicted "Addicted"
                 type-podnapisi "Podnapisi"
                 type-opensubtitles "OpenSubtitles"
                 type-subscene "Subscene"})

(def update-deep 10)

(def update-period (* 5 60 1000))

(def result-size 100)

(def default-port "3000")

(def conection-timeout 1000)

(def static-path "/static/")

(def show-name-boost 5)

(def version-boost 2)
