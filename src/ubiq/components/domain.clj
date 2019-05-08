(ns ubiq.components.domain
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [hugsql.core :as hugsql]
            [integrant.core :as ig]))

(defn- get-db-fns-map [file-path-dot-sql]
  (hugsql/map-of-db-fns file-path-dot-sql))

(defn- read-file-tree
  "Given a directory path, recursively read all files
  and return a vector containing path of all files."
  [path]
  (->> path
       io/file
       file-seq
       (filter #(.isFile %))
       (map #(.getPath %))))

(defn- inject-db [db db-fns-map]
  (into {} (map (fn [[k v]]
                  [k (partial (:fn v) db)])
                db-fns-map)))

(defn- path->file [p]
  (last (str/split p #"/")))

(defmethod ig/init-key :domain [_ {:keys [db sql-folder]}]
  (let [sql-file-paths (read-file-tree sql-folder) ; read all files in src/sql folder returns ("src/erp_app/sql/products.sql" "src/erp_app/sql/parties.sql")
        sql-files (map path->file sql-file-paths) ; figure out the name of sql file (sql folder cannot have sub folders with this setup)
        sql-folder-without-src (str/replace sql-folder #"src/" "")]
    (into {} (map (fn [file-name-dot-sql]
                    [(keyword (str/replace file-name-dot-sql #".sql" ""))
                     (inject-db db (get-db-fns-map (str sql-folder-without-src "/" file-name-dot-sql)))])
                  sql-files))))
