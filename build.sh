#!/bin/bash

mkdir -p out/artifacts

javac -d out/production/HexChess --module-path lib/javafx-sdk-25.0.3/lib --add-modules javafx.controls,javafx.fxml --enable-preview --release 23 -cp "out/production/HexChess;lib/*;lib/javafx-sdk-25.0.3/lib/*" -sourcepath src src/gui/Launcher.java

cd out/production/HexChess
jar -xf ../../../lib/javafx-sdk-25.0.3/lib/javafx.base.jar
jar -xf ../../../lib/javafx-sdk-25.0.3/lib/javafx.controls.jar
jar -xf ../../../lib/javafx-sdk-25.0.3/lib/javafx.fxml.jar
jar -xf ../../../lib/javafx-sdk-25.0.3/lib/javafx.graphics.jar
cd ../../..

jar cvfm out/artifacts/HexChess.jar src/META-INF/MANIFEST.MF -C out/production/HexChess . -C src .