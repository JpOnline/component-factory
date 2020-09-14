(ns acme.frontend.app
  (:require
    [reagent.dom]
    [re-frame.core :as re-frame]
    ))

;; Redef re-frame subscribe and dispatch for brevity
(def <sub (comp deref re-frame.core/subscribe))
(def >evt re-frame.core/dispatch)

(re-frame/reg-event-db ::set-app-state
  (fn [_ [event application-state]]
             application-state))

(re-frame/dispatch-sync [::set-app-state {:counter 0}])

(defn counter
  [app-state]
  (get-in app-state [:counter] 0))
(re-frame/reg-sub :counter counter)

(defn counted
  [app-state]
  (update-in app-state [:counter] inc))
(re-frame/reg-event-db :counted counted)

(defn test-comp []
  [:h1 "7test-comp Jp!" (<sub [:counter])])

(defn ^:dev/after-load init []
  (when-let [el (.getElementById js/document "root")]
    (reagent.dom/render [test-comp] el)))
