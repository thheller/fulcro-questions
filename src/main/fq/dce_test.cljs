(ns fq.dce-test
  (:require
    [fulcro.client.dom :as dom]
    [fulcro.client.primitives :as fp :refer (defsc)]))

(defsc Test [this props]
  {}
  (dom/div
    (dom/h1 "THIS SHOULD BE REMOVED!")))

(defn init []
  (js/console.log "THIS WILL BE ALIVE"))