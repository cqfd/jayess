(ns jayess.extensions)

(extend-type js/Buffer
  IEquiv
  (-equiv [this that]
    (and (= (.-length this) (.-length that))
         )))
