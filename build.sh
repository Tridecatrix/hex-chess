#!/bin/bash

rm -r out

mkdir -p out/artifacts

javac -d out/production/HexChess --module-path lib/javafx-sdk-25.0.3/lib --add-modules javafx.controls,javafx.fxml -cp "out/production/HexChess:lib/*:lib/javafx-sdk-25.0.3/lib/*" -sourcepath src src/gui/Launcher.java

jar cvfm out/artifacts/HexChess.jar src/META-INF/MANIFEST.MF -C out/production/HexChess . -C src .