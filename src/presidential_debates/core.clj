(ns presidential-debates.core
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :refer [print-table pprint]]
            [com.lemonodor.pronouncing :as pro]
            [opennlp.nlp :as nlp]
            [presidential-debates.load :as load]))

(def debates
  (->> (io/file "data/debates")
       (file-seq)
       (filter #(and (.isFile %) (not (.isHidden %))))
       (mapv (fn [f] (json/parse-string (slurp f) true)))))

(def sentences (nlp/make-sentence-detector "data/nlp-models/en-sent.bin"))
(def nlp-tokenize (nlp/make-tokenizer "data/nlp-models/en-token.bin"))

(defn character-frequencies
  [s]
  (->> s
       (vec)
       (frequencies)
       (map (fn [[c freq]] {:character c :freq freq :code (int c)}))
       (sort-by :freq)
       (reverse)))

(defn basic-character
  [c]
  (case (int c)
    (8211 8212) \-
    (8220 8221) \"
    (8216 8217) \'
    8230        "..."
    c))

(defn remove-special-characters
  [s]
  (->> s (mapv basic-character) (apply str)))

(defn remove-quotes
  [s]
  (-> s
      (string/replace #"\"" "")
      (string/replace #" \'|\' " " ")
      (string/replace #"^\'|\'$" "")))

(defn tokenize
  [s]
  (-> s
      (remove-special-characters)
      (remove-quotes)
      (string/replace #"'s|\(|\)|" "")
      (string/replace #"-|/" " ")
      (string/replace #"(\w+)[.,?!;:]+" "$1")
      (string/split #" +")))

(defn wordy?
  [s]
  (boolean (not-empty (re-find #"^[A-Za-z]+" s))))

(defn syllables
  ([word]
   (syllables word {}))
  ([word dictionary-extension]
   (if-let [syllables (not-empty (pro/syllable-count-for-word word))]
     (apply min syllables)
     (get dictionary-extension
          word
          (if (wordy? word) 1 0)))))

(defn dictionary-extension-candidates
  [s]
  (->> (tokenize s)
       (frequencies)
       (map (fn [[tk freq]] {:token tk :freq freq :syllables (syllables tk)}))
       (filter (comp zero? :syllables))
       (sort-by :freq)
       (reverse)))

(defn flesch-kincaid
  ([s]
   (flesch-kincaid s {}))
  ([s dictionary-extension]
   (let [sentences (sentences s)
         words (tokenize s)
         syllables (->> words (map #(syllables % dictionary-extension)) (apply +))]
     (+ (* 0.39 (/ (count words) (count sentences)))
        (* 11.8 (/ syllables (count words)))
        -15.59))))

(def debate-dictionary-extension
  (read-string (slurp "data/dictionary-extensions/debates.edn")))

(def candidates
  #{"MITT ROMNEY" "MICHAEL DUKAKIS" "AL GORE" "RICHARD NIXON" "JOHN MCCAIN" "JOHN F. KENNEDY"
    "GERALD FORD" "JOHN KERRY"  "GEORGE W. BUSH" "DONALD TRUMP" "WALTER MONDALE" "HILLARY CLINTON"
    "BOB DOLE" "RONALD REAGAN" "BILL CLINTON" "ROSS PEROT" "GEORGE H.W. BUSH" "BARACK OBAMA"
    "JIMMY CARTER" "JOHN B. ANDERSON" "JOE BIDEN"})

(def party-affiliation
  {"MITT ROMNEY"      "republican"
   "MICHAEL DUKAKIS"  "democrat"
   "AL GORE"          "democrat"
   "RICHARD NIXON"    "republican"
   "JOHN MCCAIN"      "republican"
   "JOHN F. KENNEDY"  "democrat"
   "GERALD FORD"      "republican"
   "JOHN KERRY"       "democrat"
   "GEORGE W. BUSH"   "republican"
   "DONALD TRUMP"     "republican"
   "WALTER MONDALE"   "democrat"
   "HILLARY CLINTON"  "democrat"
   "BOB DOLE"         "republican"
   "RONALD REAGAN"    "republican"
   "BILL CLINTON"     "democrat"
   "ROSS PEROT"       "independent"
   "GEORGE H.W. BUSH" "republican"
   "BARACK OBAMA"     "democrat"
   "JIMMY CARTER"     "democrat"
   "JOHN B. ANDERSON" "independent"
   "JOE BIDEN"        "democrat"})

(defn summarize-debate
  [{:keys [date transcript name] :as debate}]
  (->> transcript
       (filter (comp candidates :speaker))
       (group-by :speaker)
       (mapv (fn [[speaker statements]]
               {:speaker speaker
                :name name
                :date date
                :party (get party-affiliation speaker)
                :grade-level (flesch-kincaid (->> statements (map :statement) (string/join " "))
                                             debate-dictionary-extension)}))))
