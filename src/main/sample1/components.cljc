(ns sample1.components
  (:require [vrac.core :as v]
            [clojure.spec.alpha :as s]))

(def demo-comp
  {:id ::demo-comp
   :name "Demo Component"
   :description "A simple component to demonstrate the key features of Vrac."
   :props ['user]
   :template '[:div
               [:p "name: " (:name user)]
               (let [age (:age user)]
                 [:p "age: " age])
               [:p "friends: "
                [:ul
                 (for [buddy (:friends user)]
                   [:li
                    [:p "name: " (:name buddy)]
                    [:p "age: " (:age buddy)]])]]]})

(comment
  ; The component is matching a spec.
  (s/valid? ::v/component demo-comp)

  ; Get the eql query from the component parsed via a spec.
  (let [demo-comp (assoc demo-comp
                    :parsed-template (v/parse-template (:template demo-comp)))
        env {:components {(:id demo-comp) demo-comp}}]
    (v/get-queries env (:id demo-comp)))

  ; Render the component with the provided data as hiccup.
  (->> (:template demo-comp)
       v/parse-template
       (v/render {'user {:name "Vincent"
                         :age 42
                         :friends [{:name "Simon"
                                    :age 24}
                                   {:name "Coco"
                                    :age 7}]}})))
