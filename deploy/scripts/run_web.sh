#!/bin/bash

sudo cp -a resources/public/ /var/static/
lein with-profile production ring server-headless $@
