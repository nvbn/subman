# Subman
[![Build Status](https://travis-ci.org/nvbn/subman.svg?branch=master)](https://travis-ci.org/nvbn/subman)

Service for fast subtitle searching.

## Api

For using api send GET request like:

    http://subman.io/api/search/?query=file-name

You can specifie language by `GET` parameter `lang=name`, by default used `english`.

All languages with subtitles count available in:

    http://subman.io/api/list-languages/

You can specifie subtitles source by `GET` paramenter `source=id`, by default used `-1` (equal `all`).

All sources with ids available in [const.cljx](https://github.com/nvbn/subman/blob/master/src/cljx/subman/const.cljx).

You can get total subtitles count in:

    http://subman.io/api/count/

For decoding api response you should use [transit](https://github.com/cognitect/transit-clj).

Installation
------------

First you need to install lein, bower and elasticsearch.

Then install deps:

```bash
lein deps
lein bower install
```

Prepare assets:

```bash
lein cljsbuild once dev
lein cljx
lein garden once
```

And run with:

```bash
lein run
```

For building jar run:

```bash
lein with-profile production ring uberjar
```

For running server side tests run:

```bash
lein test
```

For client side test install phantomjs and run:

```bash
lein cljsbuild test
```
