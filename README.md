# Subman

Service for fast subtitle searching.

## Api

For using api send GET request like:

    http://subman.io/api/search/?query=file-name&format=json

You can specifie language by `GET` parameter `lang=name`, by default used `english`.

All languages with subtitles count available in:

    http://subman.io/api/list-languages/?format=json

You can specifie subtitles source by `GET` paramenter `source=id`, by default used `-1` (equal `all`).

All sources with ids available in [const.cljx](https://github.com/nvbn/subman/blob/master/src/cljx/subman/const.cljx>).

You can get total subtitles count in:

    http://subman.io/api/count/?format=json

In all api requests format can be `clojure` or `json`.

Installation
------------

First you need to install lein, bower and elasticsearch.

Then install deps:

```bash
lein deps
```

Prepare assets:

```bash
bower install
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
lein uberjar
```

For running server side tests run:

```bash
lein test
```

For client side test install phantomjs and run:

```bash
lein cljsbuild test
```
