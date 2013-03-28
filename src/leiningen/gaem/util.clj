;; from heroku

(ns leiningen.gaem.util
  (:require [clojure.java.io :as io]))
  ;; (:import (com.gaem.api.command.login BasicAuthLogin)
  ;;          (com.gaem.api.connection HttpClientConnection)
  ;;          (com.gaem.api GaemAPI)))

(def ^{:dynamic true} *app* nil)

(defn- space-key [k longest-key]
  (apply str k ":" (repeat (- longest-key (count k)) " ")))

(defn print-map [m]
  (let [longest-key (apply max (map count (keys m)))]
    (doseq [[k v] m]
      (println (space-key k longest-key) v))))

(defn prompt [prompt]
  (print prompt)
  (flush)
  (let [response (read-line)]
    (or (empty? response)
        (.startsWith response "y")
        (.startsWith response "Y"))))

(defn credentials-file []
  (io/file (System/getProperty "user.home") ".gaem" "credentials"))

;; (defn get-credentials []
;;   (let [cred (credentials-file)]
;;     (when-not (.exists cred)
;;       ;; using runtime resolve to avoid circular dependency
;;       (require 'leiningen.gaem.login)
;;       ((resolve 'leiningen.gaem.login/login)))
;;     (.split (slurp cred) "\n")))

;; (defn api []
;;   (GaemAPI/with (HttpClientConnection. (second (get-credentials)))))

(defn current-app-name []
  (or *app*
      (->> (io/file ".git/config")
           slurp
           (re-find #"git@gaem.com:(.+).git")
           second)))

;; (defn app-api []
;;   (.app (api) (current-app-name)))

;; TODO: anything using this should be exposed as a method on GaemAPI?
;; (defn execute [command]
;;   (-> (second (get-credentials))
;;       (com.gaem.api.connection.HttpClientConnection.)
;;       (.execute command)))
