(ns resolver-steps.auth)

(defn- is-logged-in? [_]
  true)

(defn logged-in? [ctx _ _]
  (if (is-logged-in?
       (get-in ctx [:request :headers :authorization]))
    (assoc-in ctx [:user] {:username "yo"})
    (assoc-in ctx [:exit] :not-logged-in)))

(defn has-role? [ctx _ _]
  (assoc-in ctx [:exit] :has-role))
