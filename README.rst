Sbuman
=======

Service for fast subtitle searching.

Installation
------------

First you need to install lein, bower, redis and elasticsearch.
Than create db in repl:

.. code-block:: clojure

    (require 'subman.models)
    (subman.models/create-index)
    (require 'subman.filler)
    (subman.filler/load-all)

Prepare assets:

.. code-block:: bash

    bower install
    lein cljsbuild once

And run with:

.. code-block:: bash

    lein ring server-headless

