(ns sample4.server.core
  (:require [reitit.core :as r]
            [ring.util.response :refer [response not-found bad-request charset]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [org.httpkit.server :as http-kit]
            [sample4.server.db :refer [db]]))

;; The different routes
(def router
  (r/router [["/api/ids/:entity-type" :api/get-ids]
             ["/api/object/:relation/:id" :api/get-object]]))

;; The handlers' definitions.
(defn get-ids-handler [{:keys [entity-type]}]
  (prn entity-type)
  (let [kw (keyword entity-type)]
    (if (contains? #{:session-ids
                     :speaker-ids
                     :room-ids
                     :tag-ids
                     :session-type-ids} kw)
      (response (kw db))
      (bad-request {:error "Unknown entity type"}))))

(defn get-object-handler [{:keys [relation id]}]
  (let [kw (keyword relation)]
    (if (contains? #{:session-by-id
                     :speaker-by-id
                     :room-by-id
                     :tag-by-id
                     :session-type-by-id} kw)
      (if-let [data (get-in db [(keyword relation) id])]
        (response data)
        (not-found {:error "object not found"}))
      (bad-request {:error "Unknown relation"}))))

;; The handlers-route relations.
;; The mapping between the handlers and the routes is done via the keyword.
(def handler-map
  {:api/get-ids (wrap-json-response get-ids-handler)
   :api/get-object (wrap-json-response get-object-handler)})

;; The request handler
(defn dispatch [request]
  (let [{{handler-key :name} :data, params :path-params} (r/match-by-path router (:uri request))]
    (when-let [handler (handler-map handler-key)]
      (handler params))))

;; A middleware wrapped around the request handler.
(def handler
  (-> dispatch
      (wrap-defaults site-defaults)))

;; Start / stop the server using the REPL.
(defonce ^:private server-stop-fn (atom nil))

(defn stop-server []
  (let [[stop _] (reset-vals! server-stop-fn nil)]
    (when stop
      (stop)
      (println "http-kit server stopped."))))

(defn start-server [port]
  (stop-server)
  (let [server-stop (http-kit/run-server #'handler {:port port})
        server-registered? (compare-and-set! server-stop-fn nil server-stop)]
    (if server-registered?
      (println (format "http-kit server is now running on port %d." port))
      (server-stop))))

(comment
  (start-server 9999)
  (stop-server)
  _)
