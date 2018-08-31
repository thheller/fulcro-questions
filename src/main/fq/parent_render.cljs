(ns fq.parent-render
  (:require
    [fulcro.client :as fc]
    [fulcro.client.primitives :as fp :refer (defsc)]
    [fulcro.client.data-fetch :as fdf]
    [fulcro.client.network :as fnet]
    [fulcro.client.mutations :as fmut :refer (defmutation)]
    [fulcro.client.routing :as froute :refer (defrouter)]
    [fulcro.client.dom :as html]

    ["react-dom" :as rdom]
    ["react" :as react]

    [clojure.string :as str]
    [fq.env :as env]))

(defmutation set-name [{:keys [id name] :as params}]
  (action [{:keys [state] :as env}]
    (js/console.log ::set-name params)
    (swap! state assoc-in [::child id ::name] name)
    ))

(defsc Child [this {::keys [id] :as props}]
  {:ident [::child ::id]

   :initial-state
   (fn [p] p)

   :query
   [::id
    ::name]}

  (js/console.log ::child props)
  (html/div
    {:onClick #(fp/transact! this `[(set-name {:id ~id :name "FOO"})])}
    (pr-str props)))

(def ui-child (fp/factory Child {:keyfn ::id}))

(defsc Root [this props]
  {:query
   [{::children (fp/get-query Child)}]

   :initial-state
   (fn [p]
     {::children [{::id 1 ::name "foo"}
                  {::id 2 ::name "bar"}]})}

  (js/console.log ::root props)
  (html/div
    (html/h1 "Root")
    (html/p "Why does the Root re-render when clicking below?")
    (for [child (::children props)]
      (ui-child child))

    ;; just doing some random work here
    (for [i (range 1000)]
      (html/div {:key i} (str i))
      )))

(defn start
  {:dev/after-load true}
  []
  (reset! env/app-ref (fc/mount @env/app-ref Root "root")))

(defn ^:export init []
  (let [app
        (fc/new-fulcro-client
          ;; the defaults access js/ReactDOM global which we don't have/want
          {:root-render
           #(rdom/render %1 %2)

           :root-unmount
           #(rdom/unmountComponentAtNode %)})]

    (reset! env/app-ref app)
    (start)))