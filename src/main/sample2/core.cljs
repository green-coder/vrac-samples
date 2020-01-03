(ns sample2.core
  (:require-macros [vrac.react.compile :refer [render-fn]])
  (:require ["react" :as react]
            ["react-dom" :as react-dom]))

; Demonstration of:
; - React render function compilation.

; Notes:
; - This was a premature optimization, an error.
; - This code won't be maintained until Vrac reaches maturity.

(def my-render-fn
  (render-fn [:div
              [:div "name: " (:name user)]
              (let [age (:age user)]
                [:div "age: " age])
              [:div "friends: "
               [:ul
                (for [buddy (:friends user)]
                  [:li
                   [:div "name: " (:name buddy)]
                   [:div "age: " (:age buddy)]])]]]))

(defn render []
  (react-dom/render
    (my-render-fn {:user {:name "Vincent"
                          :age 35
                          :friends [{:name "Coco"
                                     :age 7}
                                    {:name "Sonia"
                                     :age 18}]}})

   (js/document.getElementById "app")))

(defn ^:export reload! []
  (println "(reload!)")
  (render))

(defn ^:export init! []
  (println "(init!)")
  (render))
