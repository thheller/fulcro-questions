(ns fq.render-perf
  (:require
    [fulcro.client.dom :as html]
    [fulcro.client.primitives :as fp :refer (defsc)]
    [fulcro.client.mutations :as fmut :refer (defmutation)]
    [fq.env :as env]
    [fulcro.client :as fc]))

(defsc ListItem [this {::keys [item-id a b c] :as props}]
  {:ident
   (fn []
     [::item item-id])

   :query
   (fn []
     [::item-id
      ::a
      ::b
      ::c
      ::d])}

  (html/tr
    (html/td {:onClick #(fp/transact! this [`(do-something {:key ::a})])} a)
    (html/td {:onClick #(fp/transact! this [`(do-something {:key ::b})])} b)
    (html/td {:onClick #(fp/transact! this [`(do-something {:key ::c})])} c)
    ))

(def ui-list-item (fp/factory ListItem {:keyfn ::item-id}))

(defn make-items []
  (->> (range 25)
       (map (fn [id]
              {::item-id id
               ::a (str "a" id)
               ::b (str "b" id)
               ::c (str "c" id)}))
       (into [])))

(defsc Container [this {::keys [list-1 list-2] :as props}]
  {:query
   [{::list-1 (fp/get-query ListItem)}
    {::list-2 (fp/get-query ListItem)}]

   :initial-state
   (fn [p]
     {::list-1 (make-items)
      ::list-2 (make-items)}
     )}

  (html/div
    (html/h1 "List #1")
    (html/table
      (html/tbody
        (for [item list-1]
          (ui-list-item item))))

    (html/h1 "List #2")
    (html/table
      (html/tbody
        (for [item list-2]
          (ui-list-item item))))))

(defmutation do-something [{:keys [key] :as params}]
  (action [{:keys [state ref] :as env}]
    (swap! state update-in (conj ref key) str "x")
    ))

(defn start
  {:dev/after-load true}
  []
  (reset! env/app-ref (fc/mount @env/app-ref Container "root")))

(defn ^:export init []
  (let [app (fc/new-fulcro-client)]
    (reset! env/app-ref app)
    (start)))