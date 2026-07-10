# Hexagonal Chess

Java implementation of [Glinski's Hexagonal Chess](https://en.wikipedia.org/wiki/Hexagonal_chess#Gli%C5%84ski's_hexagonal_chess). 

Currently supports 2-6 player local multiplayer, with all game rules including draws by move repetition/no captures or
pawn movements for 50 moves. 

## How to run

Prerequisites: JDK 17 or above. See https://openjdk.org/install/.

Run the `build.sh` script to build a JAR executable:
```
./build.sh
```

Then run the `run.sh` script to run the executable (this is required since the Java command has to include the module path for JavaFX):
```
./run.sh
```

