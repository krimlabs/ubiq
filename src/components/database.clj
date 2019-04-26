(ns components.database
  (:require [clojure.java.jdbc :as jdbc]
            [hugsql.core :as hugsql]
            [integrant.core :as ig]))

(defmethod ig/init-key :database [_ {:keys [spec]}]
  {:connection (jdbc/get-connection spec)
   :spec spec})

(defmethod ig/halt-key! :database [_ database]
  (.close (:connection database)))
