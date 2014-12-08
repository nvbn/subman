FROM clojure
RUN sudo adduser --disabled-password --gecos "" subman

ENV "DEP_VERSION" 201412082047
RUN apt-get update -yqq
RUN apt-get upgrade -yqq
RUN apt-get install git software-properties-common python-software-properties -yqq --no-install-recommends
RUN add-apt-repository ppa:chris-lea/node.js  -y
RUN apt-get update -yqq
RUN apt-get install nodejs -yqq --no-install-recommends
RUN npm install -g bower

ENV "VERSION" 201412090049
WORKDIR /home/subman
USER subman
RUN git clone --depth=1 https://github.com/nvbn/subman.git
WORKDIR /home/subman/subman
RUN lein deps
RUN lein cljx once
RUN lein bower install
RUN lein with-profile production cljsbuild once >> /dev/null
RUN lein garden once
RUN lein ring  # receive dependencies of the ring plugin
CMD ["lein", "with-profile", "production", "ring", "server-headless"]
