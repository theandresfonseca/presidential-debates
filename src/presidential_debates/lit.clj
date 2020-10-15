(ns presidential-debates.lit
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :refer [print-table pprint]]
            [presidential-debates.core :as core]))

(defn load-sample
  [file-name]
  (-> file-name slurp (string/replace #"\n" " ")))

(def samples
  [{:book "charlotte's web" :file-name "data/literature/charlottes-web-chapter-1"}
   {:book "hatchet" :file-name "data/literature/hatchet-chapter-1"}
   {:book "the giver" :file-name "data/literature/the-giver-chapter-1"}
   {:book "the hunger games" :file-name "data/literature/the-hunger-games-chapter-1"}
   {:book "the outsiders" :file-name "data/literature/the-outsiders-chapter-1"}
   {:book "pride and prejudice" :file-name "data/literature/pride-and-prejudice-chapter-1"}
   {:book "the scarlet letter" :file-name "data/literature/the-scarlet-letter-chapter-1"}
   {:book "to kill a mockingbird" :file-name "data/literature/to-kill-a-mockingbird-chapter-1"}
   {:book "a brief history of time" :file-name "data/literature/a-brief-history-of-time-chapter-1"}
   {:book "on the road" :file-name "data/literature/on-the-road-chapter-1"}])

(def dictionary-extension
  "every word in our sample literature not in the CMU pronouncing dictionary"
  (read-string (slurp "data/dictionary-extensions/literature.edn")))
