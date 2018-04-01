(ns kakan.components.graphql-schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [com.stuartsierra.component :as component]))

(defn- get-resolver-map [domain]
  {:resolve-hello (fn [_ _ _]
                    "Hello World!")
   :apps (fn [_ _ _]
           ((get-in domain [:domain :apps :get-all-apps])))
   :authors (fn [_ _ _]
              ((get-in domain [:domain :authors :get-all-authors])))
   :reviews (fn [_ _ _]
              ((get-in domain [:domain :reviews :get-all-reviews])))})

(defrecord GraphqlSchema [domain]

  component/Lifecycle

  (start [component]
    (let [schema (-> "schema.edn"
                     io/resource
                     slurp
                     edn/read-string
                     (util/attach-resolvers (get-resolver-map domain))
                     schema/compile)]
      (assoc component :compiled-schema schema)))

  (stop [component]
    (assoc component :compiled-schema nil)))


(defn new-graphql-schema []
  (map->GraphqlSchema {}))
