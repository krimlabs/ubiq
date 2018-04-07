(ns kakan.components.resolver-map
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [com.stuartsierra.component :as component]))


;; "Chain is a vector of interceptors.
;;  Ex: [[:auth :logged-in?]
;;       [:domain :posts :get-all-posts]]

;;  compile-interceptors returns a function which takes 3 arguments : ctx, args and val.
;;  The returned function is bound to the resolver key in the graphql resolver map.

;;  It is assumed that the grapqhl context will contain a resolvers-root map,
;;  which has all the functions required by the interceptors.

;;  In our example above, we would expect the resolvers-root in the ctx.
;;  to be :
;;  {:auth {:logged-in? #fn}
;;   :domain {:posts {:get-all-posts #fn}}}

;;  Each function in the chain is called with 3 arguments:
;;  g {:ctx graphql context
;;     :args graphql arguments
;;     :val value of the parent resolver}
;;  intercept: data to be passed down to the next intercept (a map)
;;  next: the next function in the interceptor chaing

;;  An interceptor fn can either return a value (resolved value or error),
;;  or (next intercept), which will call the next interceptor in the chain.

(defn- compile-interceptor-chain [chain]
  (fn [ctx args value]
    (apply
     ;; compute the control fn (more about this in readme)
     (reduce (fn [compiled-chain address]
               (let [next-fn (get-in ctx (cons :root-resolver address))]
                 (when-not next-fn
                   (throw (Exception. (str "No interceptor found at " address))))
                 (fn [intercept]
                   (next-fn
                    {:ctx ctx :args args :value value}
                    intercept
                    compiled-chain))))
             (fn [_] nil)
             (reverse chain))
     ;; initial intercept
     [{}])))

(defn- compile-resolver-blueprint [blueprint]
  (map (fn [[key chain]]
         [key (fn [& args]
                "A wrapper fn which compiles the chain when the resolver is invoked."
                (apply
                 (compile-interceptor-chain chain)
                 args))]) blueprint))

(defrecord ResolverMap [domain]

  component/Lifecycle

  (start [component]
    (let [raw-blueprint (-> "resolvers.edn"
                            io/resource
                            slurp
                            edn/read-string)
          compiled-blueprint (compile-resolver-blueprint raw-blueprint)])
    (assoc component :compiled-blueprint compiled-blueprint))

  (stop [component]
    (assoc component :compiled-blueprint nil)))

(defn new-resolver-map []
  (map->ResolverMap {}))



