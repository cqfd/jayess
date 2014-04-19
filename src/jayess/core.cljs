(ns jayess.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as n]
            [cljs.core.async :refer [<! chan put!]]))

(n/enable-util-print!)

(defn -main []
  (println "hi there"))

(set! *main-cli-fn* -main)
