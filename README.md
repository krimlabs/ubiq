# Kakan
## HugSQL and GraphQL based  web app pattern

### What is Kakan ?
Kakan is a (new) way of writing graphql apps. It consists of **resolver blueprints** and **hugsql based domain** functions.

#### Resolver blueprints
Resolver blueprints defines resolvers as chain of interceptors in a static file. This chain is compiled at runtime and allows the application developer to pass around custom context. 

#### HugSQL domain
Your app is structured as *domain* of objects. Domain is nothing but a folder containing sql files.

### Kakan is not a library (yet?)
I might publish a few helper functions, but in its pristine form, Kakan is a way of building graphql and sql apps.

## Getting started
Kakan uses [Lacinia](https://github.com/walmartlabs/lacinia)'s graphql  implementation and relies heavily on [Stuart Sierra's Component](https://github.com/stuartsierra/component) framework.  

Note : If you don't have a graphql setup in place, you can read the detailed tutorial (coming soon !).

**Creating blueprint config**
Blueprint is a map where each type resolver key is bound to a chain of interceptors.

Assume you have the following `resources/schema.edn` file :
```
{:objects
 {:author
  {:fields
   {:id {:type Int}
    :name {:type String}
    :avatar_url {:type String}
    :created_at {:type String}
    :updated_at {:type String}}}}

 :queries
 {:authors
  {:type (list :author)
   :resolve :authors/all}}

```

And you need to ensure that:
- a requester should be logged in to execute `authors` query and
- each request should be logged to kibana

Create a `resources/resolvers-blueprint.edn` file :
```
{:authors/all
 [[:auth :logged-in?]
  [:logger :log]
  [:domain :authors :get-all-authors]]}
```

This map is used to compile a resolver function that executes each interceptor in order. Each interceptor has the power to yield to the next interceptor or return a response (or error). 

Each interceptor can also pass a intercept map down to the chain.

**Realising interceptors**
Once you have the blueprint of how a request should be resolved, it's time to actually write the interceptors. 

Lacinia passes 3 arguments to the resolver function. These are `ctx`, `args` & `value`. Kakan converts each interceptor chain into a function that takes in these three arguments.

Kakan also expects the graphql `ctx` to contain a `:root-resolver` map which defines each of interceptor of the chain as a function of 3 arguments : `g`, `intercept` & `next`.

In our example above, the schema should have a `:root-resolver` map that looks like:
```
 {:auth {:logged-in? (fn [g intercept next]
                       (let [user (get-user-fn (:args g))]
                         (if user
                           (next (assoc intercept :user user))
                           {:error "User invalid"})))}
  :logger {:log (fn [g _ _]
                  (log-fn (:ctx g)))}
  :domains {:authors {:get-all-authors (fn [g intecept _]
                                         (println (:user intercept)) ;; <-- Will have the user attached by auth
                                         {:authors [{:id 1 :name "Ellie"}]})}}}
 ```

 **Realising resolver fn**

Finally, the chain is passed to a `compile-interceptor-chain` function which returns a resolver-function, i.e. takes three arguments : `ctx`, `args` & `value` (and also assumes that the ctx will have a root-resovler map). This function should be bound to the resolver key while schema is being compiled. 


## Demo
The follwing code demos resolver blueprints without a graphql implementation. You can read the code if you are curious about the implementation.

```
(def root-resolver
  {:auth
   {:logged-in?
    (fn [g intercept next]
      (println "Inside Auth Function, calling (next intercept)")
      (next (assoc intercept :user {:id 1 :name "Shivek Khurana"})))}
   :domain
   {:posts
    {:get-all-posts
     (fn [g intercept next]
       (println "Inside domain function")
       (println "User is " (:user intercept))
       {:posts [{:id 1 :body "post 1"} {:id 2 :body "post 2"}]})}}
   :logger
   {:log
    (fn [g intercept next]
      (println "Logger called")
      (next intercept))}})

(def chain [[:auth :logged-in?]
            [:logger :log]
	    [:domain :posts :get-all-posts]])

(def resolver-fn (compile-interceptor-chain chain))
(def fake-ctx {:root-resolver root-resolver})

(apply resolver-fun [fake-ctx {} {}])
 ```

 The above code when run returns :
 ```
 Inside Auth Function, calling (next intercept)
 Logger called
 Inside domain function
 User is {id 1, :name Shivek Khurana}
 {:posts [{:id 1 :body "post 1"} {:id 2 :body "post 2"}]}
 ```

---
Copyright © 2018 Shivek Khurana

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
