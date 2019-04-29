(ns user
  (:require
   [clojure.java.io :as io]
   [integrant.core :as ig]
   [integrant.repl :refer [clear go halt prep init reset reset-all]]
   [aero.core :refer [read-config reader]]
   [components.graphql-server]
   [components.database]
   [components.migrator]
   [components.domain]
   [components.seeder]))


(defmethod reader 'ig/ref [_ _ value]
  (ig/ref value))

(def config
  (read-config (io/resource "config.edn") {:profile :dev}))

(integrant.repl/set-prep! (constantly (:system config)))

(def system
  integrant.repl.state/system)



