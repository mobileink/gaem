# gaem

gaem is a leiningen plugin for managing appengine-magic applications.
It replaces the plugin functionality of the appengine-magic package.

It is designed to work with the results of gaem-template, which can be
found at https://github.com/greynolds/gaem-template.

**WARNING** Alpha software.  Seems to work for me but needs more
  bullet-proofing.  I wouldn't try to use it with existing projects.
  But it is suitable for exploring appengine-magic, not to mention
  leiningen.  Documentation will have to wait, in the meantime the
  code should be clear enough provided you understand leiningen
  templates and plugins, and mustache.

  Latest version: 0.2.0-SNAPSHOT

  Collaborators welcomed.

## Installation

Download from https://github.com/greynolds/gaem, cd to the dir, and
run "lein install" (remember this is alpha stuff; if it ever makes it
to prod status it'll live on the web somewhere).  The gaem plugin is
only useful for projects created by the gaem-template, which have:

  :plugins [[gaem "0.2.0-SNAPSHOT"]

## Usage

This plugin tries to run the whole show from project.clj.

Step 1.  Create a new appengine-magic project by using the gaem template:

    $ lein new gaem myapp:app-id /path/to/gae/sdk

Here myapp is the clojure appname and app-id is the GAE application
ID.  The created app contains a few static files and two servlets, one
of which services two distinct paths.  It's a little more complicated
than the usual "Hello world" example.  Easier to subtract than to add.

Step 2.  Configure the app - generate and install appengine-web.xml and web.xml, and install other source files to the war tree.

    $ cd myapp
    $ lein gaem config  ## generates web.xml, appengine-web.xml, etc. from mustache files in etc, data in project.clj
    $ lein gaem delein  ## copy jars from .m2 to war/WEB-INF/lib.
      	   		## this is a requirement of the GAE
			## development environment.

Step 3.  compile stuff

    $ lein compile  # required, to pull in appengine-magic code
    $ lein uberjar  # necessary to run it on dev_appserver or upload

Everything must be aot compiled to run on the dev server or the cloud.
At least, that's how I got it to work.

Step 3.  Mess around wid' it.

You have two options.  One is to use the dev_appserver that comes with
the GAE SDK; the other is to use appengine-magic to run in a repl.
Since the appengine-magic test server is not the same as
dev_appserver, you should always test on the latter before uploading.
For example, the plain Jetty that appengine-magic runs in the repl
does not read the web.xml file; it runs only the servlet you pass as
an arg.

dev_appserver.sh is what comes with the GAE SDK; it runs a customized
version of Jetty.  You run it the same way you would if you were
working in java: just run the shell script as per the GAE
doccumentation.  Seems to work at the moment.

The other, more interactive way, is to run your app in a repl:

    $ lein repl

This starts the repl and launches the webapp.  You can nrepl into the
repl from your editor, or you can edit the code and then do something
like (load-file "src/test/user.clj") to load the new code for the
user servlet.

The advantage of dev_appserver is that it supports multiple servlets,
and, well, it's the official test device.  But it doesn't give you the
interactive development process of a repl.  On the repl, on the other
hand, you get interactive development, but you can only run one
servlet at a time.  Fortunately you can easily switch among servlets
by running load-file followed by ae/serve, and you can put this into a
function with a short name.  For example (from :repl-options in
project.clj):

	(defn request []
	  (do (load-file "src/test/request.clj")
	    (ae/serve test.request/test-request)))

Now you can quickly recompile and serve a different servlet in the repl:

	user=> (request)

So you can use the repl to develop your code rapidly, but you always
want to do system testing using the dev_appserver before deploying.

Eventually it would be nice to run the dev appserver
(DevAppServerMain) in the repl but that's a lot harder to do than you
might think.

Step 4.  Deploy to the cloud:

    $ lein gaem deploy

Don't forget to set the version number in project.clj first!

## Options

## Examples

...

### Bugs

...


## License

Copyright © 2013 Gregg Reynolds

Distributed under the Eclipse Public License, the same as Clojure.
