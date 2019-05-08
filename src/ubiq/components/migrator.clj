(ns ubiq.components.migrator
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [integrant.core :as ig]))

(defmethod ig/init-key :migrator [_ {:keys [db migrations-folder]}]
  (let [config {:datastore (jdbc/sql-database db)
                :migrations (jdbc/load-resources migrations-folder)}]
    (repl/migrate config)
    config))

(defmethod ig/halt-key! :migrator [_ config]
  (repl/rollback config))
