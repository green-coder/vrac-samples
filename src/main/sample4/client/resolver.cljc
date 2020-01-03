(ns sample4.client.resolver
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc :refer [defresolver]]
            #?@(:clj [[clojure.core.async :refer [go <!]]
                      [clojure.data.json :as json]
                      [clj-http.client :as http]]
                :cljs [[cljs.core.async :refer [go <!]]
                       [cljs-http.client :as http]])))

(defn fetch [env path]
  (println (str "loading " path " ..."))
  #?(:clj (-> (http/get path {:accept :json})
              :body
              (json/read-json true))
     :cljs (http/get path)))

(defresolver favorite-session-ids-resolver [{:keys [host] :as env}
                                            {:keys [] :as params}]
  {::pc/input #{}
   ::pc/output [{:session/my-favorites [:session/id]}]}
  {:session/my-favorites
   (mapv (fn [id] {:session/id id})
         ["2eb098d8-d606-4a7b-904b-c68ba5047258"
          "lightning-talk-1"
          "graph-powered-translation-model"])})

(defresolver session-ids-resolver [{:keys [host] :as env}
                                   {:keys [] :as params}]
  {::pc/input #{}
   ::pc/output [{:session/ids [:session/id]}]}
  (go
    (let [sessions (:body (<! (fetch env (str host "api/ids/session-ids"))))]
      {:session/ids (mapv (fn [id] {:session/id id}) sessions)})))

(defresolver speaker-ids-resolver [{:keys [host] :as env}
                                   {:keys [] :as params}]
  {::pc/input #{}
   ::pc/output [{:speaker/ids [:speaker/id]}]}
  (go
    (let [speakers (:body (<! (fetch env (str host "api/ids/speaker-ids"))))]
      {:speaker/ids (mapv (fn [id] {:speaker/id id}) speakers)})))

(defresolver room-ids-resolver [{:keys [host] :as env}
                                {:keys [] :as params}]
  {::pc/input #{}
   ::pc/output [{:room/ids [:room/id]}]}
  (go
    (let [rooms (:body (<! (fetch env (str host "api/ids/room-ids"))))]
      {:room/ids (mapv (fn [id] {:room/id id}) rooms)})))

(defresolver tag-ids-resolver [{:keys [host] :as env}
                               {:keys [] :as params}]
  {::pc/input #{}
   ::pc/output [{:tag/ids [:tag/id]}]}
  (go
    (let [tags (:body (<! (fetch env (str host "api/ids/tag-ids"))))]
      {:tag/ids (mapv (fn [id] {:tag/id id}) tags)})))

(defresolver session-type-ids-resolver [{:keys [host] :as env}
                                        {:keys [] :as params}]
  {::pc/input #{}
   ::pc/output [{:session-type/ids [:session-type/id]}]}
  (go
    (let [session-types (:body (<! (fetch env (str host "api/ids/session-type-ids"))))]
      {:session-type/ids (mapv (fn [id] {:session-type/id id}) session-types)})))

(defresolver session-by-id-resolver [{:keys [host] :as env}
                                     {:keys [session/id] :as params}]
  {::pc/input #{:session/id}
   ::pc/output [:session/id
                :session/type
                :session/room
                :session/start
                :session/end
                :session/pad-url
                :session/title
                :session/description
                {:session/speakers [:speaker/id]}
                {:session/tags [:tag/id]}]}
  (go
    (let [session (:body (<! (fetch env (str host "api/object/session-by-id/" id))))]
      {:session/id (:id session)
       :session/type (:type session)
       :session/room (:room session)
       :session/start (:start session)
       :session/end (:end session)
       :session/pad-url (:pad session)
       :session/title (-> session :en :title)
       :session/description (-> session :en :description)
       :session/speakers (mapv (fn [id] {:speaker/id id})
                               (:speakers session))
       :session/tags (mapv (fn [id] {:tag/id id})
                           (:tags session))})))

(defresolver speaker-by-id-resolver [{:keys [host] :as env}
                                     {:keys [speaker/id] :as params}]
  {::pc/input #{:speaker/id}
   ::pc/output [:speaker/id
                :speaker/avatar
                :speaker/name
                :speaker/bio]}
  (go
    (let [speaker (:body (<! (fetch env (str host "api/object/speaker-by-id/" id))))]
      {:speaker/id (:id speaker)
       :speaker/avatar (:avatar speaker)
       :speaker/name (-> speaker :en :name)
       :speaker/bio (-> speaker :en :bio)})))

(defresolver room-by-id-resolver [{:keys [host] :as env}
                                  {:keys [room/id] :as params}]
  {::pc/input #{:room/id}
   ::pc/output [:room/id
                :room/name]}
  (go
    (let [room (:body (<! (fetch env (str host "api/object/room-by-id/" id))))]
      {:room/id (:id room)
       :room/name (-> room :en :name)})))

(defresolver tag-by-id-resolver [{:keys [host] :as env}
                                 {:keys [tag/id] :as params}]
  {::pc/input #{:tag/id}
   ::pc/output [:tag/id
                :tag/name]}
  (go
    (let [tag (:body (<! (fetch env (str host "api/object/tag-by-id/" id))))]
      {:tag/id (:id tag)
       :tag/name (-> tag :en :name)})))

(defresolver session-type-by-id-resolver [{:keys [host] :as env}
                                          {:keys [session-type/id] :as params}]
  {::pc/input #{:session-type/id}
   ::pc/output [:session-type/id
                :session-type/name]}
  (go
    (let [session-type (:body (<! (fetch env (str host "api/object/session-type-by-id/" id))))]
      {:session-type/id (:id session-type)
       :session-type/name (-> session-type :en :name)})))

(def all-resolvers
  [favorite-session-ids-resolver ; example of global resolvers

   session-ids-resolver
   speaker-ids-resolver
   room-ids-resolver
   tag-ids-resolver
   session-type-ids-resolver

   session-by-id-resolver
   speaker-by-id-resolver
   room-by-id-resolver
   tag-by-id-resolver
   session-type-by-id-resolver

   (pc/alias-resolver :session/room :room/id)
   (pc/alias-resolver :session/type :session-type/id)])
