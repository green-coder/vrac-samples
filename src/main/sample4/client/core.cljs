(ns sample4.client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [vrac.core :as v]
            [vrac.react.core :as vr]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]
            [sample4.client.resolver :refer [all-resolvers]]
            [sample4.client.component :refer [all-components]]))

; Demonstration of:
; - Derived eql queries through multiple Vrac components.
; - Automatic data loading from a real server.


;; The Pathom parser
(def parser
  (p/parallel-parser
    {::p/env {::p/reader [p/map-reader
                          pc/parallel-reader
                          pc/open-ident-reader
                          p/env-placeholder-reader]
              ::p/placeholder-prefixes #{">"}}
     ::p/mutate pc/mutate-async
     ::p/plugins [(pc/connect-plugin {::pc/register all-resolvers})
                  p/error-handler-plugin
                  p/trace-plugin]}))

(def env
  (-> {:pathom-env {:host ""}}
      (v/with-components all-components)
      (vr/with-react-renderer)))

(defn render [component-id]
  (go
    (let [root-query (v/get-queries env component-id)
          data (<! (parser (:pathom-env env) root-query))]
      (vr/render env component-id {nil data} "app"))))

(defn ^:export reload! []
  (println "(reload!)")
  (render :coscup/root))

(defn ^:export init! []
  (println "(init!)")
  (render :coscup/root))
