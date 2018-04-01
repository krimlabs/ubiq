(ns kakan.api
  (:require [com.stuartsierra.component :as component]
            [kakan.components.database :refer [new-database]]
            [kakan.components.domain :refer [new-domain]]
            [kakan.components.graphql-schema :refer [new-graphql-schema]]
            [kakan.components.server :refer [new-server]]))

(defn system [config]
  (-> (component/system-map
       :db (new-database (:db config))
       :domain (new-domain)
       :graphql-schema (new-graphql-schema)
       :server (new-server (:enable-graphiql? config)))
      (component/system-using
       {:domain [:db]
        :graphql-schema [:domain]
        :server [:graphql-schema]})))
