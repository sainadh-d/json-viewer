(ns json-viewer.core
  (:require [reagent.core :as reagent :refer [atom]]
            ["react-json-view" :default ReactJson]))

(defonce app-state (atom {:valid-json true :json (.parse js/JSON "{}") :text "{}"}))

(defn valid-json [text]
 (try
   [true (.parse js/JSON text)]
   (catch :default e
     [false [e.message]])))

(defn update-preview
  [event]
  (let [[is-valid-json json-obj] (valid-json (:text @app-state))]
    (swap! app-state assoc :valid-json is-valid-json)
    (swap! app-state assoc :json json-obj)
    (when (= is-valid-json true)
      (do
        (swap! app-state assoc :text (.stringify js/JSON json-obj nil 2))))))

(defn update-state
  [event]
  (.preventDefault event)
  (swap! app-state assoc :text (.. event -target -value)))

(defn textarea
  []
  (let [text (:text @app-state)]
    [:textarea.textarea.has-fixed-size
     {:placeholder text
      :value text
      :class (if (= (:valid-json @app-state) true) "is-info" "is-danger")
      :cols 40
      :rows 30
      :on-change #(update-state %)}]))

(defn button []
  [:button.button.is-info {:on-click #(update-preview %)} "LOAD"])

(defn json-view []
  [:> ReactJson {:src (:json @app-state) :name false :iconStyle "circle" :sortKeys true}])

(defn header []
  [:h1 "JSON Viewer"])

(defn footer []
  [:footer "Built with cljs and react"])

(defn app []
  [:div.container
   [:div.columns
    [:div.column
     [:div.header.has-text-centered.hero.is-info.is-bold.title.is-1
      [header]]
     [:div.columns
      [:div.column.is-5
       [textarea]]
      [:div.column.is-2.has-text-centered.is-vcentered
       [button]]
      [:div.preview.column.is-5
       [json-view]]]]]
   [:div.column.footer.has-text-centered
     [footer]]])


(defn start []
  (reagent/render-component [app]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))
