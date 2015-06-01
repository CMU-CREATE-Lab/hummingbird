#!/bin/bash

java -Djna.library.path=./code/applications/dist -Djava.library.path=./code/applications/dist -cp ./code/applications/dist/hummingbird-applications.jar edu.cmu.ri.createlab.hummingbird.applications.CommandLineHummingbird;
