Sbuman
=======

Service for fast subtitle searching.

Installation
------------

First you need to install lein, bower and elasticsearch.

Then install deps:

.. code-block:: bash

    lein deps

Prepare assets:

.. code-block:: bash

    bower install
    lein cljsbuild once
    lein cljx
    lein garden once

And run with:

.. code-block:: bash

    lein ring server

