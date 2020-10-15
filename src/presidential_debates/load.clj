(ns presidential-debates.load
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn ->csv
  ([file-name data]
    (->csv file-name (->> data (mapcat keys) distinct sort) data))
  ([file-name headers data]
   (with-open [writer (io/writer file-name)]
     (->> data
          (map (fn [row] (map (partial get row) headers)))
          (cons (map name headers))
          (csv/write-csv writer)))))
