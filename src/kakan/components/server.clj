(ns kakan.components.server
  (:require [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.pedestal :refer [service-map]]
            [io.pedestal.http :as http]))

(defrecord Server [enable-graphiql? graphql-schema server]

  component/Lifecycle

  (start [component]
    (let [server  (-> (:compiled-schema graphql-schema)
                      (service-map {:graphiql enable-graphiql?})
                      http/create-server
                      http/start)]
      (assoc component :server server)))

  (stop [component]
    (when server
      (http/stop server))
    (assoc component :server nil)))


(defn new-server [enable-graphiql?]
  (map->Server {:enable-graphiql? enable-graphiql?}))
