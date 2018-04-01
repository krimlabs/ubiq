(ns user
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.namespace.repl :refer (refresh)]
            [com.stuartsierra.component :as component]
            [kakan.api :as api]))

(def config (-> "config.edn"
                io/resource
                slurp
                edn/read-string))

(def system nil)

(defn init []
  (alter-var-root #'system
    (constantly (api/system config))))

(defn start []
  (println "⬣ Started graphql api at http://localhost:8888")
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (println "⬣ Reset")
  (stop)
  (refresh :after 'user/go))
