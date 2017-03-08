(ns soundboard.core
    (:require [reagent.core :as reagent]
              [goog.events :as ev]))

(defonce app-state (atom {}))

;; Web Audio API
(defn get-context [] (:context @app-state))

(defn sound-name [identifier] (str "/sounds/" (if (keyword? identifier) (name identifier) identifier) ".wav"))

(defn sound-loaded [req sound]
  (fn [response]
    (let [context (get-context)]
        (.decodeAudioData context (.-response req)
                          (fn [data]
                            (swap! app-state assoc sound data))))))

(defn load-sound [sound]
  (let [req (js/XMLHttpRequest.)]
    (.open req "GET" (sound-name sound) true)
    (set! (.-responseType req) "arraybuffer")
    (set! (.-onload req) (sound-loaded req sound))
    (.send req)))


(defn play-sound [sound]
  (let [source (.createBufferSource (get-context))]
    (set! (.-buffer source) (get @app-state sound))
    (.connect source (.-destination (get-context)))
    (.start source 0)))

(defn init-context! [] (when (nil? (:context @app-state)) (swap! app-state assoc :context (js/AudioContext.))))

;; -------------------------
;; Views

(defn audio-button [sound]
  (let [identifier (name sound)]
      [:div.audio-button
        [:button {:on-click #(play-sound sound)} identifier]]))

(def sounds [:faster :harder :kick :sd :hihat])

(defn home-page []
  [:div
    [:h1 "TAISTE SOUNDBOARD"]
    (into [:div.sounds] (map (fn [sound] ^{:key sound} [audio-button sound]) sounds))])

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
