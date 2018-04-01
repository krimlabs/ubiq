(ns kakan.components.domain
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [hugsql.core :as hugsql]))

(defn- keywordize-path [path]
  (let [path-vector (str/split path "/")]
    (if (= (count path-vector) 1)
      (keyword (nth path-vector 0))
      (keyword (clojure.string/join "." (butlast path-vector)) (last path-vector)))))

(defn- generate-domain-map
  "Give a vector of files paths, convert them into a domain keyword
  and a path usable by hugsql."
  [files]
  (into {} (map (fn [path]
                  (let [domain-keyword (-> path
                                           (str/replace #"src/kakan/domain/" "")
                                           (str/replace #".sql" "")
                                           keywordize-path)
                        file-path (str/replace path #"src/" "")]
                    {domain-keyword file-path}))
                files)))

(defn- read-file-tree
  "Given a directory path, recursively read all files
  and return a vector containing path of all files."
  [path]
  (->> path
       io/file
       file-seq
       (filter #(.isFile %))
       (map #(.getPath %))))

(defn- inject-db [domain-map db]
  (into {} (map (fn [[key path]]
                  {key (into {} (map (fn [[key2 value2]]
                                       {key2 (partial (:fn value2) db)})
                                     (hugsql/map-of-db-fns path)))})
                domain-map)))

(defrecord Domain [db]

  component/Lifecycle

  (start [component]
    (let [sql-files (->> "src/kakan/domain"
                         clojure.java.io/file
                         file-seq
                         (filter #(.isFile %))
                         (map #(.getPath %)))
          load (fn [path] (hugsql/map-of-db-fns path))
          domain {:festivals (load "kakan/domain/festivals.sql")}]
      (println sql-files)
      (assoc component :domain domain)))

  (stop [component]
    (assoc component :domain nil)))

(defn new-domain []
  (map->Domain {}))
