(ns core.DB
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [next.jdbc :as jdbc]))

(defn read-config [filepath]
  (try
    (with-open [r (io/reader filepath)]
      (json/parse-stream r true)
      )
  (catch Exception e
    (println (ex-message e))
    {:dbtype "postgresql"
     :dbname "postgres"
     :host "localhost"
     :user "postgres"
     :password "mysecretpassword"}
    )
    )
  )

(def db-config
  (read-config "src/core/config.json"))

(def db-connection
  (try
    (jdbc/get-datasource db-config)
    (catch Exception e
      (println "an error occurred")
      (println (ex-message e))
      (throw e))
    )
  )

(def create-patients-table-sql
  "CREATE TABLE IF NOT EXISTS patients(
     id INTEGER,
     first_name TEXT NOT NULL,
     middle_name TEXT,
     second_name TEXT,
     gender_male BOOLEAN,
     date_of_birth TEXT,
     address TEXT,
     oms_policy_number TEXT
   );")

(defn ensure-patients-table-exists []
  (jdbc/execute! db-connection [create-patients-table-sql]))

(ensure-patients-table-exists)