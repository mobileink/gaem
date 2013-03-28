# sibtest

gaem is a leiningen plugin for managing appengine-magic applications.
It replaces the plugin functionality of the appengine-magic package.

It is designed to work with the results of gaem-template, which can be found at:

**WARNING** Alpha software.  Seems to work for me but needs more
  bullet-proofing.  I wouldn't try to use it with existing projects.
  But it is suitable for exploring appengine-magic, not to mention
  leiningen.  Documentation will have to wait, in the meantime the
  code should be clear enough provided you understand leiningen
  templates and plugics, and mustache.

## Installation

Download from https://github.com/greynolds/gaem, cd to the dir, and
run "lein install" (remember this is alpha stuff; if it ever makes it
to prod status it'll live on the web somewhere).  The gaem plugin is
only useful for projects created by the gaem-template, which have:

  :plugins [[gaem "0.1.0-SNAPSHOT"]

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
    $ lein gaem config

Step 3.  Mess around wid' it.

    $ lein repl

This starts the repl and launches the webapp.  You can nrepl into the
repl, or you can edit the code and then do something like
(compojure.core/compile 'myapp.user) to load the new code for the user
servlet.

Step 4.  Deploy to the cloud:

    $ lein gaem deploy

Don't forget to set the version number in project.clj first!

**ANOTHER CAVEAT**

Things seem to work ok on the dev server, but something isn't working
in the production GAE environment.  It won't serve both servlets.  I
don't know if this can be fixed or not.  But then again it has nothing
to do with the plugin functionality.  If I can't figure out how to
make multiple servlets work with appengine-magic, I'll change the
template to use a single servlet.

## Options

## Examples

...

### Bugs

...


## License

Copyright © 2013 Gregg Reynolds

Distributed under the Eclipse Public License, the same as Clojure.
