(ns user
  (:require
   [clojure.java.io :as io]
   [integrant.core :as ig]
   [integrant.repl :refer [clear go halt prep init reset reset-all]]
   [aero.core :refer [read-config reader]]
   [ubiq.components.graphql-server]
   [ubiq.components.migrator]
   [ubiq.components.domain]
   [ubiq.components.seeder]
   [ubiq.components.resolver]))


(defmethod reader 'ig/ref [_ _ value]
  (ig/ref value))

(def config
  (read-config (io/resource "erp_app/config.edn") {:profile :dev}))

(integrant.repl/set-prep! (constantly (:system config)))

(def system
  integrant.repl.state/system)

(defn start []
  (prep)
  (init)
  (reset))



