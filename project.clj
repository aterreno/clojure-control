(defproject control "0.2.3-SNAPSHOT"
  :description "A clojure DSL for system admin and deployment with many remote machines"
  :url "https://github.com/killme2008/clojure-control"
  :author "dennis zhuang(killme2008@gmail.com)"
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :dev-dependencies [[lein-exec "0.1"]]
  :shell-wrapper {:bin "bin/clojure-control"
                  :main control.main})
