(ns sample4.client.component
  (:require [vrac.core :as v :refer [defc]]))

(defc speaker-comp [speaker]
  {:id :coscup/speaker}
  [:div
   [:div "Name: " (:speaker/name speaker)]
   [:div "Bio: " (:speaker/bio speaker)]])

(defc session-comp [session]
  {:id :coscup/session}
  [:div
   [:h3 "Title: " (:session/title session)]
   [:p "Description: " (:session/description session)]
   (let [speakers (:session/speakers session)]
     (when speakers
       [:div "Speakers: "
        [:ul
         (for [speaker speakers]
           [:li
            [:coscup/speaker {:id (:speaker/id speaker)
                              :speaker speaker}]])]]))
   [:div "Room: " (:room/name session)]])

(defc root-comp []
  {:id :coscup/root}
  [:div
   [:h1.class1#some-id.class2
    {:style {:background-color "lime"}}
    "Welcome to Vrac's demo"]
   [:ul
    ;(for [room (:room/ids nil)] ; TODO: support immediate values as vectors of ids
    ;  [:li (:room/name room)])]])
    (for [session (:session/my-favorites nil)]
      [:li [:coscup/session.item {:session session}]])]])

(def all-components [speaker-comp session-comp root-comp])

(comment
  ;; TODO: add a get-queries variant which crosses components.
  ;;       Beware the trap of recursion!
  (let [env {:components (into {} (map (juxt :id identity)) all-components)}]
    (v/get-queries env (:id root-comp))))
