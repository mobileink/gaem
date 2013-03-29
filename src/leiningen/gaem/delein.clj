(ns leiningen.gaem.delein
  "delein - a gaem subtask for jarring up an appengine-magic app and copying dependencies to war/WEB-INF/lib.  (Not to be confused with the leiningen uberjar command.)    Kinda the opposite of what leiningen does.  The GAE dev environment only works if the needed jars are in war/WEB-INF/lib, so this task copies dependencies from the local ./m2 repo to the webapp lib dir."
  (:import java.io.File)
  (:require [clojure.java.io :as io]
            [stencil.core :as stencil]
            [leiningen.classpath :as cp]
            [leiningen.new.templates :as tmpl]
            [leiningen.core [eval :as eval] [main :as main]]
            [clojure.string :as string]))

(defn flat-copy-tree [from to]
  ;; (println "\nFiles in " from " to " to)
  (doseq [f (.listFiles (io/as-file from))]
    (let [fn  (.getName (io/as-file f))]
      (do ;(print "\ttgt: " f "\n")
        (if (.isDirectory f)
          (flat-copy-tree (.getPath f) to)
          (do
            ;; (print (format "\tfrom %s to %s/%s\n" f to fn))
                                        ;(print "copying\n")
            (io/make-parents to fn)
            (io/copy f (io/file to fn))))))))

(defn delein [project & args]
;;  (println "gaem deleining...")
  (println "home: " (System/getProperty "user.home"))
  (let [lib (str (:war (:gae-app project)) "/WEB-INF/lib/")
        home (System/getProperty "user.home")]
    (flat-copy-tree (str (:gae-sdk project) "/lib/user") lib)
    ;; TODO: iterate over (:dependencies project) and copy jars to lib
    (doseq [dep (:dependencies project)]
      (let [[name nbr] dep]
        (do ; (println (format "name: %s - nbr: %s" name nbr))
            ; (println (format "local repo: %s" (:local-repo project)))
            (let [[a class] (.split (str name) "/")
                  group (.replace a "." "/")
                  fpath (str home "/.m2/repository/" group "/" class "/" nbr)
                  fnm (str class "-" nbr ".jar")
                  from (.getCanonicalPath (io/as-file (str fpath "/" fnm)))]
;;              (println (format "\tgroup: %s - class: %s\n\t%s" group class fnm))
              (do
                (println (format "deleining %s" from))
                (io/copy (io/file from) (io/file lib fnm)))))))))
