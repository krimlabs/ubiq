(ns ubiq.intercepts.domain)

(defn executor [{:keys [lacinia-ctx args value interceptor-args] :as ctx}]
  (let [db-fns (:db-fns lacinia-ctx)
        fn-to-execute (get-in db-fns (:path interceptor-args) nil)]
    (prn (type (into () (fn-to-execute))))
    (assoc-in ctx [:exit] (into () (fn-to-execute)))))





