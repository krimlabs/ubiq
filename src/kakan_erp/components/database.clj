(ns kakan-erp.components.database
  (:require [clojure.java.jdbc :as jdbc]
            [hugsql.core :as hugsql]
            [integrant.core :as ig]))

(defmethod ig/init-key :database [_ {:keys [spec]}]
  (jdbc/get-connection spec))

(defmethod ig/halt-key! :database [_ connection]
  (.close connection))
