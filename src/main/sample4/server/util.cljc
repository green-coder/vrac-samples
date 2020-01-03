(ns sample4.server.util)

(defn index-one-by-one [f coll]
  (into {}
        (map (fn [x] [(f x) x]))
        coll))

(defn index-one-by-many [f coll]
  (into {}
        (mapcat (fn [x]
                  (mapv (fn [k] [k x])
                        (f x))))
        coll))

(defn index-many-by-one [f coll]
  (group-by f coll))

(defn index-many-by-many [f coll]
  (persistent!
    (reduce (fn [res x]
              (let [ks (f x)]
                (reduce (fn [res k]
                          (assoc! res k (conj (get res k []) x)))
                        res
                        ks)))
            (transient {})
            coll)))
