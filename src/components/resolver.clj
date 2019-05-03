(ns components.resolver
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [integrant.core :as ig]
            [resolver-steps.auth :as auth]))

(defn- handle-exit-wrapper [f ctx]
  (or (:exit ctx) (f ctx)))

(defn- compile-steps [steps]
  (let [wrapped-functions (map
                           #(partial
                             handle-exit-wrapper
                             (resolve (symbol (str "resolver-steps." (namespace %) "/" (name %)))))
                           steps)
        resolver-fn (apply comp (reverse wrapped-functions))]

    ;; return a function that converts lacinia's three args resolver to ubiq's single arg resolver
    (fn [lacinia-ctx args value]
      (resolver-fn
       {:lacinia-ctx lacinia-ctx
        :args args
        :value value}))))

(defn get-resolvers-config []
  (-> "resolvers.edn"
      io/resource
      slurp
      edn/read-string))

(defmethod ig/init-key :resolver [_ {:keys [domain]}]
  (let [resolvers-config (get-resolvers-config)]
    (zipmap
     (keys resolvers-config)
     (map compile-steps (vals resolvers-config)))))


