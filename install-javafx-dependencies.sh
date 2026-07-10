#!/bin/bash

for module in 'base' 'controls' 'fxml' 'graphics' 'media' 'swing' 'web'; do
  echo $module
  mvn install:install-file -Dfile=lib/javafx-sdk-25.0.3/lib/javafx.${module}.jar -DgroupId=javafx.${module} -DartifactId=javafx.${module} -Dversion=25.0.3 -Dpackaging=jar -DgeneratePom=true
done