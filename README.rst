Subman
=======

Service for fast subtitle searching.

Api
----

For using api send GET request like:

.. code-block:: bash

    http://subman.io/api/search/?query=file-name&format=json&lang=english


Installation
------------

First you need to install lein, bower and elasticsearch.

Then install deps:

.. code-block:: bash

    lein deps

Prepare assets:

.. code-block:: bash

    bower install
    lein cljsbuild once dev
    lein cljx
    lein garden once

And run with:

.. code-block:: bash

    lein run

For building jar run:

.. code-block:: bash

    lein uberjar

For running server side tests run:

.. code-block:: bash

    lein midje

For client side:

.. code-block:: bash

    npm install -g karma
    karma

