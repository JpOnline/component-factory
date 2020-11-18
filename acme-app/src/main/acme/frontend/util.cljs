(ns acme.frontend.util
  (:require
    [re-frame.core :as re-frame]
    [reagent.core]
    [reagent.dom]
    ))

;; Redef re-frame subscribe and dispatch for brevity
(def <sub (comp deref re-frame.core/subscribe))
(def >evt re-frame.core/dispatch)


(defonce already-defined-components (atom {})) ;; This is used for hot reloading.

(defn- reload-existent-custom-elements! [name]
  (doseq [to-reload (-> js/document (.getElementsByTagName name))
          new-el [(-> js/document (.createElement name))]]
    (-> to-reload (.replaceWith new-el))))

(defn define-custom-element! [{:keys [element-name view-component props-to-observe props-to-reflect]}]
  (swap! already-defined-components #(assoc % element-name view-component))
  (if (js/window.customElements.get element-name)
    (reload-existent-custom-elements! element-name)
    (let [attach-shadow (fn [^js obj] (do (set! (.-shadow obj) (.attachShadow obj #js {:mode "open"})) obj))
          set-reagent-props (fn [^js obj] (do (set! (.-reagent-atom-props obj) (reagent.core/atom {})) obj))
          render (fn [^js obj] (do
                                 (js/console.log "rendered")
                                 (reagent.dom/render [(fn [] [(@already-defined-components element-name) @(.-reagent-atom-props obj)])] (.-shadow obj)) obj))
          ;; defines the constructor function, which is the "class" object used by the customElements api
          component (fn component []
                      (-> (js/Reflect.construct js/HTMLElement #js [] component) ;; super()
                          attach-shadow
                          set-reagent-props
                          render))]
      ;; Static methods
      (js/Object.defineProperties component #js {:observedAttributes #js {:get #(clj->js (mapv name props-to-observe))}})

      (set! (.-prototype component)
            ;; establishes prototype hierarchy
            (js/Object.create (.-prototype js/HTMLElement)
                              (clj->js
                                (into {:connectedCallback
                                       #js {:configurable true
                                            :value (fn []
                                                     (this-as this
                                                              (js/console.log "Conectado")))}
                                       :attributeChangedCallback
                                       #js {:value (fn [attr-name old-value new-value]
                                                     (this-as ^js this
                                                        (swap! (.-reagent-atom-props this) assoc (keyword attr-name) new-value))
                                                     (js/console.log "Prop changed" attr-name old-value new-value))}}
                                       (for [prop props-to-observe]
                                         {prop #js {:get (fn []
                                                           (js/console.log "get prop" prop)
                                                           (this-as this (-> this (.getAttribute (name prop)))))
                                                    :set (fn [val]
                                                           (js/console.log "set val" prop val)
                                                           (this-as ^js this
                                                              (swap! (.-reagent-atom-props this) assoc prop val)
                                                              (cond
                                                                (and val ((set props-to-reflect) prop)) (-> this (.setAttribute (name prop) val))
                                                                ((set props-to-reflect) prop) (-> this (.removeAttribute (name prop))))))}})))))

      ;;finally, defines the component with these values
      (js/window.customElements.define element-name component))))


(defn with-lifecycle-fn
  "Wrap component in the create-class fn so the react component-did-mount
  hook can be used."
  [[n {:keys [component-did-mount component-did-update]} :as to-render]]
  (reagent.core/create-class
    {:reagent-render #(into [] (-> to-render
                                   (update 1 dissoc :component-did-mount)
                                   (update 1 dissoc :component-did-update)))
     (when component-did-mount :component-did-mount) component-did-mount
     (when component-did-update :component-did-update) component-did-update}))

(defn with-update-fn
  "Wrap component in the create-class fn so the react component-did-mount
  hook can be used."
  [[n {:keys [component-did-update]} :as to-render]]
  (reagent.core/create-class
    {:reagent-render #(into [] (update-in to-render [1]
                                          dissoc :component-did-update))
     :component-did-update component-did-update}))


(defn evtJs [events]
  (let [clj-events (js->clj events)
        kw-events (mapv #(if (= (first %) \:) (keyword (subs % 1)) %) clj-events)]
    (re-frame.core/dispatch kw-events)))

(defn addDataChangedHandler [query callback-fn]
  (let [clj-query (js->clj query)
        kw-query (mapv #(if (= (first %) \:) (keyword (subs % 1)) %) clj-query)
        watcher-key (random-uuid)]
    (add-watch (re-frame/subscribe kw-query) watcher-key
             (fn [key atom old-state new-state]
               (callback-fn old-state new-state)))
    watcher-key)) ;; Return key for eventual remove-watch call.
