(ns components.resolver
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [integrant.core :as ig]
            [intercepts.auth :as auth]
            [intercepts.domain :as domain]))

(defn- handle-exit-wrapper [{:keys [i-fn i-args]} ctx]
  (let [ctx-with-i-args (assoc-in ctx [:interceptor-args] i-args)]
    ;; if :exit key is set in the ctx, do nothing till the end of intercepts is reached
    ;; and then return the value of key to callerx
    (or (:exit ctx-with-i-args) (i-fn ctx-with-i-args))))

(defn- resolve-interceptor-fn-symbol [s]
  ;; assuming that all intercepts will be under intercepts. namespace
  ;; and will be imported in this domain! (Can improve DX here by checking that ns exists)
  (resolve (symbol (str "intercepts." (namespace s) "/" (name s)))))

(defn- intercept->interceptor-fn [intercept]
  ;; allow for different kinds of interceptor config and normalise it here
  (cond
    (symbol? intercept) {:i-fn (resolve-interceptor-fn-symbol intercept)
                         :i-args {}}
    (map? intercept) {:i-fn (resolve-interceptor-fn-symbol (:fn intercept))
                      :i-args (:args intercept)}
    (fn? intercept) {:i-fn intercept
                     :i-args {}}))

(defn- compile-intercepts [intercepts]
  ;; need to append identity fn to intercept because exit handler works before executing a function and
  ;; if the last function is returning a result, it'd never reach exit handler. So the last identity helps
  ;; pick up the actual last result.
  (let [intercepts-with-identity (conj intercepts identity)
        wrapped-functions
        (map
         #(partial
           handle-exit-wrapper
           (intercept->interceptor-fn %))
         intercepts-with-identity)
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

(defmethod ig/init-key :resolver [_ {:keys []}]
  (let [resolvers-config (get-resolvers-config)]
    (zipmap
     (keys resolvers-config)
     (map compile-intercepts (vals resolvers-config)))))


