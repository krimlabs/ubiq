(ns user
  (:require
   [clojure.java.io :as io]
   [integrant.core :as ig]
   [integrant.repl :refer [clear go halt prep init reset reset-all]]
   [kakan-erp.components.graphql-server]
   [kakan-erp.components.database]))

(integrant.repl/set-prep! (constantly (ig/read-string (slurp (io/resource "config.edn")))))

