(ns core
  (:require [reagent.core :as r]
            ["react-dom/client" :as ReactDOM]
            ["@mui/material/Button" :default Button]
            ["react-icons/fa" :refer [FaBeer]]
            ["recharts" :refer [LineChart Line XAxis YAxis CartesianGrid Tooltip Legend]]
            ["framer-motion" :refer [motion]]
            ["animejs" :refer [animate createScope createSpring createDraggable createTimer]]))

;; (defn app []
;;   [:div [:div {:class "text-xl font-medium text-black"} "ChitChat"]
;;    [:p {:class "text-slate-500:}"} "You have a new message!"]])

;; (defn app []
;;   [:div
;;    [:> Button {:variant "contained" :color "primary"} "Click Me"]
;;    [:div
;;     [:> FaBeer {:size 40 :color "orange"}]
;;     " Cheers!"]]
;;   )

;; (defn app []
;;   [:> LineChart {:width 500 :height 300 :data [{:name "A" :value 100} {:name "B" :value 200}]}
;;    [:> CartesianGrid {:strokeDasharray "3 3"}]
;;    [:> XAxis {:dataKey "name"}]
;;    [:> YAxis]
;;    [:> Tooltip]
;;    [:> Legend]
;;    [:> Line {:type "monotone" :dataKey "value" :stroke "#8884d8"}]])

;; (defn app []
;;   [:> motion.div {:initial {:opacity 0}
;;                   :animate {:opacity 1}
;;                   :transition {:duration 1}}
;;    "Hello, World"])


(js/console.log "Animation config:" #js {:scale     #js [{:value    1.25
                                                          :duration 200
                                                          :easing   "easeInOutQuad"}
                                                         {:value    1
                                                          :duration 200
                                                          :easing   "easeInOutQuad"}]
                                         :loop      true
                                         :loopDelay 250})

(defn create-animation-config []
  (clj->js
   {:scale [{:to 1.25
             :ease "inOut(3)"
             :duration 200}
            {:to 1
             :ease (createSpring #js {:stiffness 300})}]
    :loop true
    :loopDelay 250}))

(defn app []
  (let [root (r/atom nil)        ; 替代 useRef(null)
        scope (r/atom nil)       ; 替代 useRef(null)
        rotations (r/atom 0)]    ; 替代 useState(0)

    (r/create-class
     {:component-did-mount
      (fn []
        (reset! scope
                (-> (createScope #js {:root @root})
                    (.add (fn [self]
                            ;; 创建弹跳动画
                            (animate ".logo" (create-animation-config))

                            ;; 使 logo 可拖拽
                            (createDraggable ".logo"
                                             (clj->js
                                              {:container [0 0 0 0]
                                               :releaseEase (createSpring #js {:stiffness 200})}))

                            ;; 注册 rotateLogo 方法
                            (.add self "rotateLogo"
                                  (fn [i]
                                    (animate ".logo"
                                             (clj->js
                                              {:rotate (* i 360)
                                               :ease "out(4)"
                                               :duration 1500})))))))))

      :component-will-unmount
      (fn []
        (when (and @scope (.-revert ^js @scope))
          (.revert ^js @scope)))

      :reagent-render
      (fn []
        [:div {:ref #(reset! root %)}
         [:div.large.centered.row
          [:img {:src "react.svg"
                 :class "logo react"
                 :alt "React logo"}]]
         [:div.medium.row
          [:fieldset.controls
           [:button
            {:on-click (fn []
                         (swap! rotations inc)
                         (.methods.rotateLogo ^js @scope @rotations))}
            (str "旋转次数: " @rotations)]]]])})))

(defn timer-component []
  (let [current-time (r/atom 0)
        iteration-count (r/atom 0)]

    (r/create-class
     {:component-did-mount
      (fn []
        (createTimer
         (clj->js
          {:duration 1000
           :loop true
           :frameRate 30
           :onUpdate (fn [self]
                       (reset! current-time (.-currentTime self)))
           :onLoop (fn [self]
                     (reset! iteration-count (.-_currentIteration self)))})))

      :reagent-render
      (fn []
        [:div
         [:div.value-container
          [:span "当前时间: "]
          [:span.value {:class "time"} @current-time]]
         [:div.value-container
          [:span "循环次数: "]
          [:span.value {:class "count"} @iteration-count]]])})))

(defn init []
  (let [root (.getElementById js/document "app")]
    (-> (.createRoot ReactDOM root)
        (.render (r/as-element [timer-component])))))