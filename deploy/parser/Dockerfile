FROM clojure
RUN sudo adduser --disabled-password --gecos "" subman

ENV "DEP_VERSION" 201412082047
RUN apt-get update -yqq
RUN apt-get upgrade -yqq
RUN apt-get install git -yqq --no-install-recommends
RUN mkdir -p /usr/src
WORKDIR /usr/src

ENV "VERSION" 201412090049
WORKDIR /home/subman
USER subman
RUN git clone --depth=1 https://github.com/nvbn/subman.git
WORKDIR /home/subman/subman
RUN lein deps
RUN lein cljx once
CMD ["lein", "with-profile", "production", "run"]
