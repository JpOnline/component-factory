(ns acme.frontend.comp3
  (:require
    [re-frame.core :as re-frame]
    [acme.frontend.util :as util :refer [<sub >evt]]
    ))

(defn counted
  [app-state]
  (update-in app-state [:counter] inc))
(re-frame/reg-event-db :counted counted)

(defn component-x2 []
  [:button
   {:onClick #(>evt [:counted])}
   "inc"])

(defn ^:dev/after-load my-comp []
  (util/define-custom-element! {:element-name "component-x2"
                                :view-component component-x2
                                })
  )
