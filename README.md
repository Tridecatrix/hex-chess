https://www.youtube.com/watch?v=bgR3yESAEVE

basic premise:
hex chess with a 2 player mode, a 3 player mode and a 6 player mode

# Minimal Viable Product

Just 2 player mode
- a GUI
  - Coloration
  - Indicating possible moves
- local multiplayer
- All rules are implemented:
  - Rules for each piece to move
  - Check detection and handling (limit possible moves to only those which eliminate check)
  - Checkmate detection
  - Pawns:
    - Pawn promotion
    - Double space movement for pawns (including ability to move if on a starting square)
    - En passant
  - Rooks
  - Knight
  - Bishop (incl. weird diagonals)
  - Queen
  - King
- Stalemate
- Starting positions

The version of 2 player hex chess I'm making is specifically the Glinski variant https://en.wikipedia.org/wiki/Hexagonal_chess#Gli%C5%84ski's_hexagonal_chess
- URL above provides a reference for the rules

# Features to add

- Online multiplayer
  - Premoving and arrows
- 3 player and 6 player modes
  - https://en.wikipedia.org/wiki/Hexagonal_chess#Three_player_hexagonal_chess_variants

# Planning

https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller