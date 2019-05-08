(ns ubiq.intercepts.auth)

(defn- is-logged-in? [_]
  true)

(defn logged-in? [ctx]
  (prn ::logged-in?)
  (if (is-logged-in?
       (get-in ctx [:request :headers :authorization]))
    (assoc-in ctx [:user] {:username "yo"})
    (assoc-in ctx [:exit] :not-logged-in)))

(defn has-role? [ctx]
  (prn ::has-role?)
  (assoc-in ctx [:roles] {:role :hai}))
