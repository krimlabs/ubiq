(ns kakan.components.database
  (:require [com.stuartsierra.component :as component]))

(defrecord Database [config db]

  component/Lifecycle

  (start [component]
    (let [db {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname (str "//" (:host config) ":" (:port config) "/" (:database config))
              :user (:user config)
              :password (:password config)}]
      (assoc component :db db)))

  (stop [component]
    (assoc component :db nil)))

(defn new-database [config]
  (map->Database {:config config}))
