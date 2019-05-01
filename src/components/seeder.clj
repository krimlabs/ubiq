(ns components.seeder
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [hugsql.core :as hugsql]
            [faker.name]
            [faker.address]))

(defn- seed-parties [domain count]
  (log/info "Seeding parties")
  (let [insert-party (get-in domain [:parties :insert-party])]
    (doall (map (fn [_]
                  (insert-party
                   {:name (str (faker.name/first-name) " " (faker.name/last-name))
                    :address (faker.address/street-address)}))
                (range count)))))

(defn- seed-products [domain]
  (log/info "Seeding products")
  (let [insert-product (get-in domain [:products :insert-product])]
    (doall (map #(insert-product {:name %})
                ["Fan 15" "Fan 12" "Fan 17" "Hub 12"]))))

(defmethod ig/init-key :seeder [_ {:keys [seed-data? seed-count domain]}]
  (when seed-data?
    (seed-parties domain (:parties seed-count))
    (seed-products domain)))
