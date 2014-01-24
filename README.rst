Sbuman
=======

Service for fast subtitle searching.

Installation
------------

First you need to install lein and elasticsearch.
Than run:
.. code-block:: clojure
    (require 'subman.models)
    (subman.models/create-index)
    (require 'subman.filler)
    (subman.filler/load-all)

