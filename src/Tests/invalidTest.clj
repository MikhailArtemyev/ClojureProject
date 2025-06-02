(ns invalidTest
  (:require
    [clojure.test :refer :all]
    [cheshire.core :as json]
    [core.patient-crud.SQL :as sql]
    [validTest :refer [setup, get-last-patient]]
    )
  )

(defn new-test-patient [id name date pol-num]
  {:id            id
   :first_name    name
   :second_name   "Test"
   :date_of_birth date
   :oms_policy_number pol-num})

(defn insert-test-patient [id name date pn]
  (let [test-patient (new-test-patient id name date pn)
        json-str (json/generate-string test-patient)]
    (sql/create-patient json-str)))


(deftest test-wrong-name
  (testing "Creating an invalid patient name"
    (setup)
      (let [result (insert-test-patient 100 13 "2004-11-18" "1234123412341234")]
        (is (= result "invalid")))
    (setup)
    )
  )

(deftest test-wrong-date
  (testing "Creating an invalid patient dob"
    (setup)
    (let [result (insert-test-patient 100 "John" "20-1100-18" "1234123412341234")]
      (is (= result "invalid")))
    (setup)
    )
  )

(deftest test-wrong-oms
  (testing "Creating an invalid patient oms number"
    (setup)
    (let [result (insert-test-patient 100 "John" "2004-11-18" "nan")]
      (is (= result "invalid")))
    (setup)
    )
  )

(deftest test-wrong-update
  (testing "Creating an invalid patient oms number"
    (setup)
    (insert-test-patient 100 "John" "2004-11-18" "1234123412341234")
    (let [patient (get-last-patient)
          id (:id patient)
          updated-json (json/generate-string (assoc patient :first_name 13310))
          result (sql/update-patient id updated-json)
          ]
      (is (= result "invalid")))
    (setup)
    )
  )


