(ns leiningen.gaem.config
  "config - a gaem subtask for configuring a gae app"
  (:import java.io.File)
  (:require [clojure.java.io :as io]
            [stencil.core :as stencil]
            [leiningen.classpath :as cp]
            [leiningen.new.templates :as tmpl]
            [leiningen.core [eval :as eval] [main :as main]]
            [clojure.string :as string]))

(defn copy-tree [from to]
  ;; (println "\nFiles in " from " to " to)
  (doseq [f (.listFiles (io/as-file from))]
    (let [fn  (.getName (io/as-file f))]
      (do ;(print "\ttgt: " f "\n")
        (if (.isDirectory f)
          (copy-tree (.getPath f) (str to "/" (.getName f)))
          (do
            ;; (print (format "\tfrom %s to %s/%s\n" f to fn))
                                        ;(print "copying\n")
            (io/make-parents to fn)
            (io/copy f (io/file to fn))))))))

;;       (with-open [of (io/writer (io/file to fn))]
;; )))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Override leiningen templates.clj functions:
(def render-text stencil/render-string)
(defn renderer [name]
  (fn [template & [data]]
    (let [path (string/join "/" [name template])
          ;; a (println (str "+name: " name))
          ;; b (println (str "template: " template))
          ;; c (println (str "path: " path))
          ]
      (if-let [resource (io/resource path)]
        (if data
          (render-text (tmpl/slurp-resource resource) data)
          (io/reader resource))
        (main/abort (format "Template resource '%s' not found." path))))))
;;;;;;;;;;;;;;;;
;; The original code (in leiningen/src/leiningen/new/templates.clj)
;; creates the project dir.  That's no good for us - we're already in
;; the proj dir, we want to process templates and put the results in
;; the war dir.
(defn- template-path [name path data]
  (io/file name (render-text path data)))
(def ^{:dynamic true} *dir* nil)
(defn ->files
  [{:keys [name] :as data} & paths]
  ;; (let [dir (or *dir*
  ;;               (.getPath (io/file
  ;;                          (System/getProperty "leiningen.original.pwd") name)))]
  ;;   (if (or *dir* (.mkdir (io/file dir)))
  (let [dir "./"]
    (doseq [path paths]
      (if (string? path)
        (.mkdirs (template-path dir path data))
        (let [[path content] path
              path (template-path dir path data)]
          (.mkdirs (.getParentFile path))
          (io/copy content (io/file path)))))))
                                        ;    (println "Could not create directory " dir ". Maybe it already exists?"))))
;; end of overrides

(defn config
  "copy/transform files into the war dir structure - 'lein gaem :config'

This task is designed to support the need to distribute all the other
files you need for a (java) webapp: what goes in the war dir, WEB-INF,
etc.  The idea is to control all that via the project.clj file.  At
the moment all it does is create the xml config
files (appengine-web.xml and web.xml) and write them to <war>/WEB-INF.
The files are created by processing the (stencil/mustache) templates
in <project>/.project using the data fields from project.clj.  So you
should not edit the files directly; if you need to make a
change (e.g. change the version number), edit the project.clj and then
run 'lein gaem config'."
  [project & args]
  (do
    ;; (println (str "compiling " (:name project)))
    ;; (jar/jar project)
    (let [render (renderer ".") ;; (:name project))
          config (:gae-app project)
          static_exclude (:pattern (:exclude (:statics   config)))
          resource_exclude (:pattern (:exclude (:resources   config)))
          ;; foo (println static_exclude)
          ;; NOTE:  data maps of gaem-template and gaem plugin must match
          data {:name	(:name project) ;; :name required by ->files
                :project	(:name project)
                :projname	(:name project)
                :app-id		(:id config)
                :display-name	(:display-name config)
                :version	(:dev   (:version config))
                :war		(:war	    config)

                :servlets	[(:servlets config)]

                ;; TODO: conditional processing of include/expire/exclude
                :static_src    (:src (:statics config))
                :static_dest   (:dest (:statics config))
                :static_include_pattern	(:pattern
                                         (:include (:statics config)))
                :static_expire	(:expire (:include (:statics config)))
                :static_exclude (if (nil? static_exclude)
                                  false
                                  {:static_exclude_pattern
                                   (:pattern (:exclude (:statics   config)))})

                :resource_src  (:src (:resources config))
                :resource_dest (:dest (:resources config))
                :resource_include_pattern (:pattern
                                           (:include (:resources config)))
                :resource_expire (:expire (:include (:resources config)))
                :resource_exclude (if (nil? resource_exclude)
                                    false
                                    {:resource_exclude_pattern
                                     (:pattern (:exclude (:resources config)))})

                :welcome	(:welcome   config)
                :threads	(:threads   config)
                :sessions	(:sessions  config)
                :java-logging	(:java-logging config)}]

      ;;      (println (format "copying static files from src tree to war tree"))
      ;; TODO:  use {{statics}} instead of hardcoded paths
      (copy-tree "src/main/public" "war")
      ;; (copy-tree "src/main/public/css" "war/css")
      ;; (copy-tree "src/main/public/js" "war/js")

      ;;      (println (format "installing config XML files to %s"
      ;;                       (string/join "/" ["war" "WEB-INF"])))

      ;;      (println (format "copying logging specs from etc to war tree"))
      ;;      (println (format "copying resource files from src tree to war tree"))
      (do
        (->files data
                 ;; to file  		from template

                 ["{{war}}/WEB-INF/appengine-web.xml"
                  (render "etc/appengine-web.xml.mustache" data)]
                 ;; (render (str "etc" (:name project)
                 ;;              "/appengine-web.xml.mustache")
                 ;;         data)]

                 ["{{war}}/WEB-INF/web.xml"
                  (render "etc/web.xml.mustache" config)]

                 ["{{war}}/{{welcome}}"
                  (render (render-text "{{static_src}}/{{welcome}}" data))]

                 ;; TODO copy the entire static src tree


                 ;; ["{{war}}/{{static_dest}}/css/{{project}}.css"
                 ;;  (render (render-text "{{static_src}}/css/{{project}}.css" data))]
                 ;; ["{{war}}/{{static_dest}}/js/{{project}}.js"
                 ;;  (render (render-text "{{static_src}}/js/{{project}}.js" data))]

                 ;; TODO: handle binary files??
                 ["{{war}}/favicon.ico"
                  (render (render-text "{{resource_src}}/favicon.ico" data))]

                 ["{{war}}/WEB-INF/{{java-logging}}"
                  (render (render-text "etc/{{java-logging}}" data))]

                 )
        (println "ok"))
      )))
