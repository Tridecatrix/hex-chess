Application class is the root

Applications have init(), stop() and start(Stage stage) methods; the start one is mandatory

Stage is the top level JavaFX controller
- belongs to a parent Window
- has style, title, owner, modality, and size options
- you set a scene to be held in the stage, and then call stage.show()

Scene is the container for all content in a scene graph
- you set a root Node, which may be a group, and then everything else in the scene is part of a tree of nodes
- Group and Region are two types of Parent nodes. Leaf nodes are the actual elements, Parent nodes are for determining layout
- Polygon is a type of leaf node

Pane is the Parent class and is the basic layout type which exposes its children and can have things added/removed from them freely
- StackPane is a type of pane that allows overlapping objects
- VBox is a type of Pane which creates a vertical column of objects
- ScrollPane provides a cropped gui of its contents which the user can scroll through

# References

https://www.youtube.com/watch?v=KQoP9M7silY&list=PL4h6ypqTi3RR_bhBk6PtLfD83YkaJXXxw&index=3 making an undo button
https://www.youtube.com/watch?v=heAxEXeXPdo&list=PL4h6ypqTi3RR_bhBk6PtLfD83YkaJXXxw&index=4 graphical directory browser
https://github.com/AlmasB/FXTutorials 

javaFX API docs https://openjfx.io/javadoc/26/

# First step: making the title screen

I want to make a title screen for setting game settings, and have a button that switches to the actual game
- this is also a small exercise in JavaFX

# Second step: making the game screen

Board will be rendered as a BoardView containing the views for each tile of the board, and all the pieces on the board
- boardview will be an instance of Group
- add each tile and it's X/Y coordinates
- BoardView contains an instance of Board
- include a "render()" function which updates all of the positions of pieces according to underlying positions
- include a "selectPiece()" function which selects one of the pieces and highlights all tiles associated to legal moves from this position
  - select on click
- allow either clicking a highlighted tile OR dragging the piece to its destination 


