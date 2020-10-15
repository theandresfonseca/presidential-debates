(ns presidential-debates.core-test
  (:require [clojure.test :refer [are deftest]]
            [presidential-debates.core :as core]))

(deftest tokenize-test
  (are [arg ans] (= (core/tokenize arg) ans)
    "" [""]
    "some simple words" ["some" "simple" "words"]
    "multiple. sentences." ["multiple" "sentences"]
    "commas, too." ["commas" "too"]
    "\"quotes too?\" I asked." ["quotes" "too" "I" "asked"]
    "don't trip on contractions" ["don't" "trip" "on" "contractions"]
    "compound-words" ["compound" "words"]
    "i'm interrupted-" ["i'm" "interrupted"]
    "trails off..." ["trails" "off"]
    "quotes' trouble" ["quotes" "trouble"]
    "'ole faithful" ["ole" "faithful"]
    "white  space" ["white" "space"]
    "this: is silly" ["this" "is" "silly"]))

(deftest syllables-test
  (are [args ans] (= (apply core/syllables args) ans)
    ["--"] 0
    ["flentl"] 1
    ["flentl" {"flentl" 2}] 2))
