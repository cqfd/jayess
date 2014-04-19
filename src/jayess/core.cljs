(ns jayess.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as n]
            [cljs.core.async :refer [<! chan put!]]))

(n/enable-util-print!)

(defn -main []
  (println (= (js/Buffer #js [1 2 3 4])
              (js/Buffer #js [1 2 3]))))

(set! *main-cli-fn* -main)
