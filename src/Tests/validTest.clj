(ns validTest
  (:require
    [clojure.test :refer :all]
    [cheshire.core :as json]
    [core.patient-crud.SQL :as sql]
    [core.patient-crud.DB :refer [db-connection]]
    )
  )

(defn new-test-patient [id pn]
  {:id            id
   :first_name    "Name"
   :second_name   "Test"
   :date_of_birth "2025-05-21"
   :oms_policy_number pn})

(defn insert-test-patient [id pn]
  (let [test-patient (new-test-patient id pn)
        json-str (json/generate-string test-patient)]
    (sql/create-patient json-str)))

(defn get-last-patient []
  (last (sql/get-all-patients)))

(defn setup []
  (next.jdbc.sql/delete! db-connection :patients {:second_name "Test"}))

(deftest test-create-patient
  (testing "Create a patient"
    (setup)
    (let [pn "1234567890000000"
          json-str (json/generate-string (new-test-patient 11 pn))]
      (sql/create-patient json-str)
      (let [patients (sql/get-all-patients)]
        (is (some #(= (:oms_policy_number %) pn) patients))))
    (setup)
    )
  )

(deftest test-update-patient
  (setup)
  (testing "Update a patient's first name"
    (insert-test-patient 11 "123")
    (let [patient (get-last-patient)
          id (:id patient)
          updated-json (json/generate-string (assoc patient :first_name "Updated"))]
      (sql/update-patient id updated-json)
      (let [updated (get-last-patient)]
        (is (= "Updated" (:first_name updated))))))
  (setup)
  )

(deftest test-delete-patient
  (setup)
  (testing "Delete a patient"
    (insert-test-patient 123 "1234567890000000")
    (let [patient (get-last-patient)
          id (:id patient)]
      (sql/delete-patient id)
      (let [patients (sql/get-all-patients)]
        (is (not-any? #(= (:id %) id) patients)))))
  (setup)
  )

