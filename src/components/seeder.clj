(ns components.seeder
  (:require [integrant.core :as ig]
            [hugsql.core :as hugsql]))

(defmethod ig/init-key :seeder [_ {:keys [seed-data?]}])
