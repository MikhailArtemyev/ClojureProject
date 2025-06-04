(ns core.API
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [core.SQL :as sql]
            [ring.middleware.cors :refer [wrap-cors]]
            )
  )

(defroutes app-routes
           (POST "/patients" request
             (let [body (:body request)
                   result (sql/create-patient (cheshire.core/generate-string body))]
               (if (not= result "invalid")
                 (do {:status 201 :body {:message "Patient created"}})
                 {:status 400 :body {:message "Invalid patient"}}
                 )
               )
             )

           (GET "/patients" []
             {:status 200 :body (sql/get-all-patients)})

           (PUT "/patients/:id" [id :as request]
             (let [body (:body request)
                   result (sql/update-patient (Integer/parseInt id) (cheshire.core/generate-string body))]
               (if (not= result "invalid")
                 (do {:status 200 :body {:message "Patient updated"}})
                 {:status 400 :body {:message "Invalid request"}}
                 )
               )
             )

           (DELETE "/patients/:id" [id]
             (sql/delete-patient (Integer/parseInt id))
             {:status 200 :body {:message "Patient deleted"}})

           (route/not-found {:status 404 :body {:error "Not Found"}}))

(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :post :put :delete])
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      ))

(defn -main []
  (run-jetty app {:port 3000 :join? false}))
