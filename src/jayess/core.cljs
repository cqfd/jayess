(ns jayess.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as n]
            [cljs.core.async :refer [<! chan close! put!]]))

(n/enable-util-print!)

(def B js/Buffer)
(def tcp (n/require "net"))

(defn channelize [conn]
  (let [c (chan)]
    (. conn (on "error" (fn [_] (close! c))))
    (. conn (on "end" (fn [] (close! c))))
    (. conn (on "data" (fn [buf]
                         (. conn pause)
                         (put! c buf (fn [success?]
                                       (if success?
                                         (. conn resume)                                         
                                         (. conn destroy)))))))
    c))

(defn lines [in]
  (let [out (chan)]
    (go (loop [line (B. 0) buf (<! in) i 0]
          (cond
           (nil? buf) (close! out)
           (>= i (. buf -length)) (recur (. B (concat (array line buf))) (<! in) 0)
           (= 0xa (aget buf i)) (do (>! out (. B (concat (array line (. buf (slice 0 (inc i)))))))
                                    (recur (B. 0) (. buf (slice (inc i))) (inc i)))
           :else (recur line buf (inc i)))))
    out))

(defn echo-lines [conn]
  (let [ls (lines (channelize conn))]
    (go (loop []
          (when-let [l (<! ls)]
            (. conn (write l))
            (recur))))))

(defn -main []
  (let [server (. tcp createServer)]
    (go (let [c (chan)]
          (close! c)
          (put! c "hmm" (fn [wat]
                          (. js/console (log "wat" wat))))))
    (. server (on "connection" echo-lines))
    (. server (listen 45678))))

(set! *main-cli-fn* -main)
