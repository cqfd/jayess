(ns jayess.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as n]
            [cljs.core.async :refer [<! chan close! put!]]))

(n/enable-util-print!)

(def B js/Buffer)
(def tcp (n/require "net"))

(defn channelize
  "socket -> chan buf

  Returns a channel that yields data received from a socket."
  [conn]
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

(defn lines
  "chan buf -> chan buf

  Takes a channel that yields arbitrary buffers and returns a channel
  that yields new-line delimited buffers."
  [in]
  (let [out (chan)]
    (go (loop [line (B. 0) buf (<! in) i 0]
          (cond
           (nil? buf)
           (close! out)

           (>= i (. buf -length))
           (let [bigger-line (. B (concat (array line buf)))
                 new-buf (<! in)]
             (recur bigger-line new-buf 0))

           (= 0xa (aget buf i))
           (let [end-of-line (. buf (slice 0 (inc i)))
                 rest-of-buf (. buf (slice (inc i)))]
             (>! out (. B (concat (array line end-of-line))))
             (recur (B. 0) rest-of-buf (inc i)))
           
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
    (. server (on "connection" echo-lines))
    (. server (listen 45678))))

(set! *main-cli-fn* -main)
