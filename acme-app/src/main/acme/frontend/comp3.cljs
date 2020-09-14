(ns acme.frontend.comp3
  (:require
    [reagent.dom]
    [re-frame.core :as re-frame]
    ))

;; Redef re-frame subscribe and dispatch for brevity
(def <sub (comp deref re-frame.core/subscribe))
(def >evt re-frame.core/dispatch)

(defonce already-defined-components (atom {})) ;; This is used for hot reloading.
(defn define-custom-element! [name view-component]
  (if (js/window.customElements.get name)
    (swap! already-defined-components #(assoc % name view-component))
    (let [set-shadow (fn [^js obj] (do (set! (.-shadow obj) (.attachShadow obj #js {:mode "open"})) obj))
          _ (swap! already-defined-components #(assoc % name view-component))
          render (fn [^js obj] (do (reagent.dom/render [(@already-defined-components name)] (.-shadow obj)) obj))
          ;; defines the constructor function, which is the "class" object used by the customElements api
          component (fn component []
                      (-> (js/Reflect.construct js/HTMLElement #js [] component)
                          set-shadow
                          render))]
      (set! (.-prototype component)
            ;; establishes prototype hierarchy
            (js/Object.create (.-prototype js/HTMLElement) #js {}))

      ;;finally, defines the component with these values
      (js/window.customElements.define name component))))

(defn component-x2 []
  [:button
   {:onClick #(>evt [:counted])}
   "inc"])

(defn ^:dev/after-load my-comp []
  (define-custom-element! "component-x2" component-x2)
  )
