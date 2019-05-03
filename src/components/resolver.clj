(ns components.resolver
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [integrant.core :as ig]
            [resolver-steps.auth :as auth]))

(defn handle-exit-wrapper
  ([f ctx] (handle-exit-wrapper f ctx {} {}))
  ([f ctx args value]
   (prn "-> " f ctx args value)
   (or (:exit ctx) (f ctx args value))))

(defn compile-steps [steps]
  (apply comp
         (reverse
          (map
           #(partial
             handle-exit-wrapper
             (resolve (symbol (str "resolver-steps." (namespace %) "/" (name %)))))
           steps))))

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


