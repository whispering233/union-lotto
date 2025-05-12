(ns core
  (:require [reagent.core :as r]
            ["react-dom/client" :as ReactDOM]))

(defn app []
  [:div "Hello, World"])

(defn init []
  (let [root (.getElementById js/document "app")]
    (-> (.createRoot ReactDOM root)
        (.render (r/as-element [app])))))