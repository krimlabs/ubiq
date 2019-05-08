(ns ubiq.components.seeder
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [faker.name]
            [faker.address]))

(defn- seed-parties [db-fns count]
  (log/info "Seeding parties")
  (let [insert-party (get-in db-fns [:parties :insert-party])]
    (doall (map (fn [_]
                  (insert-party
                   {:name (str (faker.name/first-name) " " (faker.name/last-name))
                    :address (faker.address/street-address)}))
                (range count)))))

(defn- seed-products [db-fns]
  (log/info "Seeding products")
  (let [insert-product (get-in db-fns [:products :insert-product])]
    (doall (map #(insert-product {:name %})
                ["Fan 15" "Fan 12" "Fan 17" "Hub 12"]))))

(defmethod ig/init-key :seeder [_ {:keys [seed-data? seed-count db-fns]}]
  (when seed-data?
    (seed-parties db-fns (:parties seed-count))
    (seed-products db-fns)))
