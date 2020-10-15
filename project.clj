(defproject presidential-debates "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[cheshire "5.8.1"]
                 [clj-fuzzy "0.4.1"]
                 [clojure-opennlp "0.5.0"]
                 [com.lemonodor/pronouncing "0.0.5"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.csv "0.1.4"]]
  :repl-options {:init-ns presidential-debates.core})
