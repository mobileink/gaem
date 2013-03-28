(ns leiningen.gaem.deploy
  "deploy - a gaem subtask for deploying a gae app to the server (appcfg.sh update)"
  (:import com.google.appengine.tools.admin.AppCfg)
  (:use [leiningen.core.main :only [abort]])
  (:require [leiningen.jar :as jar]
            [leiningen.classpath :as cp]
                                        ;          [com.google.appengine/appengine-tools-api "1.7.4"]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [leiningen.core.eval :as eval]
            [leiningen.core.main :as main]
            [leiningen.new.templates :as tmpl]
            [stencil.core :as stencil]))

(defn deploy
  "upload app the the prod servers - 'lein gaem :deploy'"
  [project & args]
  (println "updating to prod cloud")
  (eval/eval-in-project project

                        ;; AppCfg will barf if run from the jar in ~/.m2
                        ;; set appengine.sdk.root to prevent this

                        (let [appengine-sdk (:gae-sdk project)
                              appengine-sdk (cond (nil? appengine-sdk) (if-let [from-env (System/getenv "APPENGINE_HOME")]
                                                                         from-env
                                                                         (abort (str "deploy "
                                                                                          (str "no App Engine SDK specified: "
                                                                                               "set :sdk in :gaem-app-config stanza of project.clj, "
                                                                                               "or APPENGINE_HOME in the environment"))))
                                                  (string? appengine-sdk) appengine-sdk
                                                  (map? appengine-sdk) (let [username (System/getProperty "user.name")
                                                                             raw (get appengine-sdk username)]
                                                                         (when (nil? raw)
                                                                           (abort (format "no valid App Engine SDK directory defined for user %s"
                                                                                               username)))
                                                                         raw))
                              appengine-sdk (let [appengine-sdk (io/as-file appengine-sdk)]
                                              (when-not (.isDirectory appengine-sdk)
                                                (abort (format "%s is not a valid App Engine SDK directory"
                                                                    appengine-sdk)))
                                              appengine-sdk)]
                              ;; version (if (not (nil? version))
                              ;;           version ; just use the given version
                              ;;           (let [versions (if (contains? project :appengine-app-versions)
                              ;;                            (:appengine-app-versions project)
                              ;;                            (abort (str task-name
                              ;;                                             " requires :appengine-app-versions"
                              ;;                                             " in project.clj")))]
                              ;;             (cond
                              ;;              ;; not a map
                              ;;              (not (map? versions))
                              ;;              (abort "bad format for :appengine-app-versions")
                              ;;              ;; check the given app-name
                              ;;              (not (contains? versions app-name))
                              ;;              (abort (format ":appengine-app-versions does not contain %s"
                              ;;                                  app-name))
                              ;;              ;; looks fine now
                              ;;              :else (versions app-name))))]
                          (do
                            (System/setProperty "appengine.sdk.root" (.getCanonicalPath appengine-sdk))
                            ;; (.addShutdownHook (Runtime/getRuntime) (proxy [Thread] []
                            ;;                                          (run [] (when (.exists out-appengine-web-xml)
                            ;;                                                    (.delete out-appengine-web-xml)))))
                            (println "running AppCfg")
                            (let [path (.getCanonicalPath (io/as-file (:war (:gae-app project))))]
                              ;; TODO: support args to AppCfg
                              (AppCfg/main (into-array [ "--enable_jar_splitting"
                                                        "update"
                                                        path])))))))
