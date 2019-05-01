(ns components.domain
  (:require [clojure.java.io :as io]
            [hugsql.core :as hugsql]
            [integrant.core :as ig]))

(defn- get-db-fns-map [domain-entity]
  (hugsql/map-of-db-fns
   (io/resource (str "sql/" (name domain-entity) ".sql"))))

(defn- inject-db [db db-fns-map]
  (into {} (map (fn [[k v]]
                  [k (partial (:fn v) db)])
                db-fns-map)))

(defmethod ig/init-key :domain [_ {:keys [db]}]
  {:parties (inject-db db (get-db-fns-map :parties))
   :products (inject-db db (get-db-fns-map :products))})
