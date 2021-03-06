(ns chess.rules
  (:require [chess.board :as board :refer :all]))

(defn all-king-moves [[x y]]
  (for [dx (range -1 2)
        dy (range -1 2)
        :when (not (and (= dx 0)
                        (= dy 0)))]
   { :from [x y] :to [(+ x dx) (+ y dy)] }))

(defn all-rook-moves [[x y]]
  (flatten (for [d (range -7 8)
                 :when (not (= d 0))]
             [{ :from [x y] :to [(+ x d) y] }
              { :from [x y] :to [x (+ y d)] }])))

(defn all-queen-moves [[x y]]
  (flatten (for [d (range -7 8)
                 :when (not (= d 0))]
             [{ :from [x y] :to [(+ x d) y] }
              { :from [x y] :to [x (+ y d)] }
              { :from [x y] :to [(+ x d) (- y d)] }
              { :from [x y] :to [(+ x d) (+ y d)] }])))

(defn all-piece-moves [[piece coords]]
  (cond (board/king? piece) (all-king-moves coords)
        (board/rook? piece) (all-rook-moves coords)
        (board/queen? piece) (all-queen-moves coords)
        :else []))

(defn possible-move? [board move]
  (and (board/valid-coords? (:to move))
       (not (board/same-color? board (:from move) (:to move)))
       (board/nothing-between? board (:from move) (:to move))))

(defn all-possible-moves [board white]
  (filter #(possible-move? board %)
          (flatten (map all-piece-moves (board/pieces board white)))))

(defn check? [board white]
  (let [king-coords (board/king-coords board white)
        moves (all-possible-moves board (not white))]
    (some #(= king-coords (:to %)) moves)))

(defn valid-move? [board white move]
  (not (check? (board/apply-move board move) white)))

(defn moves [board white]
  (filter #(valid-move? board white %)
          (all-possible-moves board white)))

(defn checkmate? [board white]
  (and (check? board white)
       (empty? (moves board white))))

(defn stalemate? [board white]
  (and (not (check? board white))
       (empty? (moves board white))))
