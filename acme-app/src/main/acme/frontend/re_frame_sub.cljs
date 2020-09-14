(ns acme.frontend.re-frame-sub
  (:require
    [reagent.core]
    [reagent.dom]
    [re-frame.core :as re-frame]
    [acme.frontend.util :as util :refer [<sub >evt]]
    ))

(defn re-frame-sub [{:keys [prop-sub-pair]}]
  [(util/with-update-fn
     [:slot (into {:component-did-update #(doseq [[prop sub] (js->clj prop-sub-pair)]
                                                 (goog.object/set (-> % reagent.dom/dom-node .assignedElements first) prop (<sub [(keyword sub)])))}
                  (when prop-sub-pair (into {} (for [[prop sub] (js->clj prop-sub-pair)]
                                                 {prop (<sub [(keyword sub)])}))))])])

#_(defn re-frame-sub [{:keys [prop-sub-pair]}]
  [:slot (when prop-sub-pair (into {} (for [[prop sub] (js->clj prop-sub-pair)]
                                          {prop (<sub [(keyword sub)])})))]
    )

(defn ^:dev/after-load def-comp []
  (util/define-custom-element! {:element-name "re-frame-sub"
                                :view-component re-frame-sub
                                :props-to-observe [:prop-sub-pair]}))
