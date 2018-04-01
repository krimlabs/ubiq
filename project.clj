(defproject kakan "0.1.0-SNAPSHOT"
  :description "Kakan - HugSQL & GraphQL based web app framework"
  :url "https://github.com/shivekkhurana/kakan"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:source-paths ["dev"]}}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.postgresql/postgresql "9.4.1207"]
                 [com.layerware/hugsql "0.4.5"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.walmartlabs/lacinia "0.25.0"]
                 [com.walmartlabs/lacinia-pedestal "0.7.0"]])
