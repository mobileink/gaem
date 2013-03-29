(ns leiningen.gaem.dev_appserver
  "dev_appserver - a gaem subtask for running the gae local dev_appserver"
  (:import com.google.appengine.tools.KickStart
           com.google.appengine.tools.development.DevAppServerMain)
  (:require [clojure.java.io :as io]
            [stencil.core :as stencil]
            [leiningen.classpath :as cp]
            [leiningen.new.templates :as tmpl]
            [leiningen.core [eval :as eval] [main :as main]]
            [clojure.string :as string]))

(defn dev_appserver
  "dev_appserver"
  [& args]
  (println "running dev_appserver...")
;; SDK_BIN="`dirname "$0" | sed -e "s#^\\([^/]\\)#${PWD}/\\1#"`" # sed makes absolute
;; SDK_LIB="$SDK_BIN/../lib"
;; JAR_FILE="$SDK_LIB/appengine-tools-api.jar"

;; if [ ! -e "$JAR_FILE" ]; then
;;     echo "$JAR_FILE not found"
;;     exit 1
;; fi

;; java -ea -cp "$JAR_FILE" \
;;   com.google.appengine.tools.KickStart \
;;   com.google.appengine.tools.development.DevAppServerMain "$@"

  
)
