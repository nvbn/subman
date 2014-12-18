FROM clojure
MAINTAINER Vladimir Iakovlev <nvbn.rm@gmail.com>

RUN adduser --disabled-password --gecos "" subman
RUN adduser subman sudo

ENV "VERSION" 2014_12_18_22_58

RUN apt-get update -yqq
RUN apt-get upgrade -yqq
RUN apt-get install software-properties-common python-software-properties -yqq --no-install-recommends
RUN add-apt-repository ppa:chris-lea/node.js  -y
RUN apt-get update -yqq
RUN apt-get install nodejs -yqq --no-install-recommends
RUN npm install -g bower

WORKDIR /home/subman
COPY . /home/subman/code
RUN chown -R subman code
USER subman
WORKDIR /home/subman/code

RUN lein deps
RUN lein cljx once
RUN lein bower install
RUN lein with-profile production cljsbuild once >> /dev/null 2>> /dev/null
RUN lein garden once

VOLUME /var/static
