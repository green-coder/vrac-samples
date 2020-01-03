(ns sample4.server.db
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [sample4.server.util :refer [index-one-by-one
                                         index-many-by-one
                                         index-many-by-many]]))

;(-> (slurp "https://api2019.coscup.org/programs.json"))
;    (json/read-json true)
;    (set/rename-keys {:session_types :session-types})
;    #(spit "resources/coscup-programs.edn" %))

(def raw-data
  (-> (io/resource "coscup-programs.edn")
      (slurp)
      (edn/read-string)))

;; Note: this db is not normalized on purpose, to represent a real world legacy API.
(def db
  (let [{:keys [sessions
                speakers
                rooms
                tags
                session-types]} raw-data]
    {:session-ids (mapv :id sessions)
     :speaker-ids (mapv :id speakers)
     :room-ids (mapv :id rooms)
     :tag-ids (mapv :id tags)
     :session-type-ids (mapv :id session-types)

     :session-by-id (index-one-by-one :id sessions)
     :speaker-by-id (index-one-by-one :id speakers)
     :room-by-id (index-one-by-one :id rooms)
     :tag-by-id (index-one-by-one :id tags)
     :session-type-by-id (index-one-by-one :id session-types)
     :sessions-by-speaker-id (index-many-by-many :speakers sessions)
     :sessions-by-room-id (index-many-by-one :room sessions)
     :sessions-by-tag-id (index-many-by-many :tags sessions)}))
