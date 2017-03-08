(ns soundboard.core
    (:require [reagent.core :as reagent]))

;; -------------------------
;; Views

(def sounds [:faster :harder :stronger :kick :sd :hihat])

(defn audio-button [sound]
  (let [identifier (name sound)]
    [:div.audio-button
     [:audio {:id identifier}
       [:source {:src (str "sounds/" identifier ".wav")}]]
     [:button
       {:on-click
         (fn [e]
           (.play
             (.getElementById js/document identifier)))}
       identifier]]))

(defn home-page []
  [:div
    (map (fn [sound]
           ^{:key sound}
           [audio-button sound])
         sounds)])

;; <div><h2>Terve vaan Asteriski ja Digit!</h2></div>

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
