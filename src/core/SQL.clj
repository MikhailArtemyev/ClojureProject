(ns core.SQL
  (:require
    [next.jdbc.sql :as sql]
    [cheshire.core :as json]
    [core.DB :refer [db-connection]]
    [clojure.string :as str]
            )
  )

;; validate a patient on create or update
(defn validate [patient_data]
  (let [
        valid_oms (== (count(:oms_policy_number patient_data)) 16)
        valid_fst_name (string? (:first_name patient_data))
        valid_lst_name (string? (:second_name patient_data))
        split (str/split (:date_of_birth patient_data) #"-")
        valid_dob (and (and (== (count(get split 0)) 4) (== (count(get split 1)) 2)) (== (count(get split 2)) 2))
        ]
    (and valid_oms (and valid_fst_name (and valid_lst_name valid_dob)))
    )
  )

;; JSON with patient's data -> insert into the table +
(defn create-patient [request_json]
  (let [data (json/parse-string request_json true)]
    (if (validate data)
      (sql/insert! db-connection :patients data)
      "invalid"
      )
    )
  )

;; gets read of "patients/"
(defn unqualify-keys [m]
  (into {}
        (for [[k v] m]
          [(keyword (name k)) v])))

;; returns a list of all patients +
(defn get-all-patients []
   (let [rows (sql/query db-connection ["select * from patients"])]
     (map unqualify-keys rows)))

;; updates a patient with specified id
(defn update-patient [id request_json]
  (let [data (json/parse-string request_json true)]
    (if (validate data)
        (sql/update! db-connection :patients data {:id id})
        "invalid"
      )
    )
  )


;; deletes a patient with a specified id
(defn delete-patient [id]
  (sql/delete! db-connection :patients {:id id})
  )
