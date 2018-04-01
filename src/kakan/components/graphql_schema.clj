(ns kakan.components.graphql-schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [com.stuartsierra.component :as component]))

(defrecord GraphqlSchema [domain]

  component/Lifecycle

  (start [component]
    (let [schema (-> "schema.edn"
                     io/resource
                     slurp
                     edn/read-string
                     (util/attach-resolvers {:resolve-hello (get-in domain [:domain :festivals :get-all-artists])})
                     schema/compile)]
      (assoc component :compiled-schema schema)))

  (stop [component]
    (assoc component :compiled-schema nil)))


(defn new-graphql-schema []
  (map->GraphqlSchema {}))
