Subman
=======

Service for fast subtitle searching.

Api
----

For using api send GET request like:

.. code-block:: bash

    http://subman.io/api/search/?query=file-name&format=json
    
You can specifie language by :code:`GET` parameter :code:`lang=name`, by default used :code:`english`.

All languages with subtitles count available in:

.. code-block:: bash

    http://subman.io/api/list-languages/?format=json
    
You can specifie subtitles source by :code:`GET` paramenter :code:`source=id`, by default used :code:`-1` (equal :code:`all`).

All sources with ids available in `const.cljx <https://github.com/nvbn/subman/blob/master/src/cljx/subman/const.cljx>`_.

You can get total subtitles count in:

.. code-block:: bash

    http://subman.io/api/count/?format=json
    
In all api requests format can be :code:`clojure` or :code:`json`.

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
    npm install -g jasmine-core
    lein cljsbuild once test
    karma start --single-run

