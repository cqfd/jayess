(defproject jayess "0.1.0-SNAPSHOT"
  :description "js interop sandbox"
  :url "https://github.com/happy4crazy/jayess"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]]

  :node-dependencies [[bitfield "*"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-npm "0.4.0"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "jayess"
              :source-paths ["src"]
              :compiler {
                :output-to "out/jayess.js"
                :target :nodejs
                :optimizations :simple}}]})
