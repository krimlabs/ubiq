(ns ubiq.intercepts.domain)

(defn executor [{:keys [lacinia-ctx args value interceptor-args] :as ctx}]
  (let [domain (:domain lacinia-ctx)
        fn-to-execute (get-in domain (:path interceptor-args) nil)]
    (prn (type (into () (fn-to-execute))))
    (assoc-in ctx [:exit] (into () (fn-to-execute)))))





