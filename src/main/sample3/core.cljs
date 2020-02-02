(ns sample3.core
  (:require [vrac.core :as v :refer [defc]]
            [vrac.react.core :as vr]))

; Demonstration of:
; - The defc macro for defining vrac components.
; - Vrac components referring to one another.
; - Attributes and props in Vrac templates.
; - Props passing between Vrac templates.

;; TODO: support this template syntax
;; [(if (:done? item) :del :div)
;;  (:description item)]

(defc todo-item [item]
  {:id :todo/item
   :name "Todo item"
   :description "An item in a todo list."}
  [:div
   ;{:style {:text-decoration-line "line-through"}}
   (if (:done? item)
     [:del (:description item)]
     [:div (:description item)])])

(defc todo-list [list]
  {:id :todo/list
   :name "Todo list"
   :description "A todo list, listing its items."}
  [:div
   [:h3 (:name list)]
   [:ul
    (for [item (:items list)]
      [:li
       [:todo/item {:item item}]])]])

(def all-components [todo-item todo-list])

(def env
  (-> {}
      (v/with-components all-components)
      (vr/with-react-renderer)))

(def todo-list-props
  {:list {:name "Things to do"
          :items [{:description "Sleep" :done? true}
                  {:description "Do sport" :done? false}
                  {:description "Release Vrac" :done? false}]}})

(defn render []
  (vr/render env :todo/list todo-list-props "app"))

(defn ^:export reload! []
  (println "(reload!)")
  (render))

(defn ^:export init! []
  (println "(init!)")
  (render))
