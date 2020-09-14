(ns acme.frontend.webcomp
  (:require
    [acme.frontend.util :as util :refer [<sub >evt]]
    [re-frame.core :as re-frame]
    ))

(defn comp4 [{:keys [p1]}]
  [:h1 "P1: " p1])

(defn component-x [{:keys [p2]}]
  [:<>
   [comp4 {:p1 p2}]
   [:h1 "component x" (<sub [:counter])]])

(defn ^:dev/after-load my-comp []
  (println "my-comp5")
  (util/define-custom-element! {:element-name "component-x"
                                :view-component component-x
                                :props-to-observe [:p2]
                                :props-to-reflect [:p2]
                                }))
