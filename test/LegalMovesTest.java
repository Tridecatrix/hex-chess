import static org.junit.jupiter.api.Assertions.assertEquals;

import model.Board;
import model.Position;
import model.Move;
import model.piece.Piece;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class LegalMovesTest {
    Set<String> movesToString(Set<Move> moves) {
        Set<String> strs = new HashSet<>(moves.size());
        for (Move move : moves) {
            strs.add(move.toPos.toString());
        }
        return strs;
    }

    void assertMovesEqual(Set<String> expected, Board board, String fromPos) {
        Set<String> actual = movesToString(board.getLegalMovesFromPos(new Position(fromPos)));

        Set<String> diffExtra = new HashSet<>(actual);
        diffExtra.removeAll(expected);

        Set<String> diffMissing = new HashSet<>(expected);
        diffMissing.removeAll(actual);

        assertEquals(expected, actual, "\n" + board
                                               + "Moves from position " + fromPos + " don't match; actual is missing moves "
                                                  + diffMissing + " and has extra moves " + diffExtra);
    }

    @Test
    void pawnStartingWhite() {
        Board board = new Board(List.of("Pf5"));
        assertMovesEqual(Set.of("f6", "f7"), board, "f5");
    }

    @Test
    void pawnStartingBlack() {
        Board board = new Board(List.of("pf7"));
        assertMovesEqual(Set.of("f5", "f6"), board, "f7");
    }

    @Test
    void pawnCapturesWhite() {
        Board board = new Board(List.of("Pc3", "qb3", "rd4"));
        assertMovesEqual(Set.of("b3", "c4", "d4"), board, "c3");
    }

    @Test
    void pawnCapturesBlack() {
        Board board = new Board(List.of("pg6", "Rh5", "Qf6"));
        assertMovesEqual(Set.of("g5", "h5", "f6"), board, "g6");
    }

    @Test
    void pawnNoSelfCaptureWhite() {
        Board board = new Board(List.of("Pf5", "Rg5"));
        assertMovesEqual(Set.of("f6", "f7"), board, "f5");
    }

    @Test
    void pawnNoSelfCaptureBlack() {
        Board board = new Board(List.of("pf7", "rg6"));
        assertMovesEqual(Set.of("f6", "f5"), board, "f7");
    }

    @Test
    void pawnAllBlockedWhite() {
        Board board = new Board(List.of("Pf5", "Rg5", "Qe5", "Bf6"));
        assertMovesEqual(Set.of(), board, "f5");
    }

    @Test
    void pawnAllBlockedBlack() {
        Board board = new Board(List.of("ph7", "ph6", "ng7", "bi6"));
        assertMovesEqual(Set.of(), board, "h7");
    }

    @Test
    void knightSimple1() {
        // knight in corner of board
        Board board = new Board(List.of("Na1"));
        assertMovesEqual(Set.of("d2", "d3", "c4", "b4"), board, "a1");
    }

    @Test
    void knightSimple2() {
        // knight on edge of board
        Board board = new Board(List.of("Nk3"));
        assertMovesEqual(Set.of("j1", "i2", "h4", "h5", "i6", "j6"), board, "k3");
    }

    @Test
    void knightSimple3() {
        // knight on top corner of board
        Board board = new Board(List.of("nf11"));
        assertMovesEqual(Set.of("d8", "e8", "g8", "h8"), board, "f11");
    }

    @Test
    void knightComplex1() {
        // knight is in centre of board (f6) and surrounded fully by pieces
        Board board = new Board(List.of("Nf6", "Pf7", "Pf5", "Pg5", "Pe5", "Pe6", "Pg6"));
        assertMovesEqual(Set.of("e8", "g8",
                            "d7", "c5",
                            "h7", "i5",
                            "c4", "d3",
                            "i4", "h3",
                            "e3", "g3"),
                     board, "f6");
    }

    @Test
    void knightComplex2() {
        // same as knightComplex1 but with black pieces
        Board board = new Board(List.of("nf6", "pf7", "pf5", "pg5", "pe5", "pe6", "pg6"));
        assertMovesEqual(Set.of("e8", "g8",
                            "d7", "c5",
                            "h7", "i5",
                            "c4", "d3",
                            "i4", "h3",
                            "e3", "g3"),
                board, "f6");
    }

    @Test
    void rookSimple1() {
        // rook in corner of board and blocked in most directions
        Board board = new Board(List.of("Ra1", "Pb1", "Pb2"));
        assertMovesEqual(Set.of("a2", "a3", "a4", "a5", "a6"), board, "a1");
    }

    @Test
    void rookSimple2() {
        // rook on corner of board v2 (including some captures)
        Board board = new Board(List.of("Ra1", "Pa2", "pe2", "Pd1"));
        assertMovesEqual(Set.of("b2", "c3", "d4", "e5", "f6", "g6", "h6", "i6", "j6", "k6", "b1", "c1"), board, "a1");
    }

    @Test
    void rookComplex() {
        Board board = new Board(List.of("Rf6", "pc3", "Pd6", "pf8", "Pj6", "Pf2"));
        assertMovesEqual(Set.of("f7", "f8",
                            "g6", "h6", "i6",
                            "g5", "h4", "i3", "j2", "k1",
                            "f5", "f4", "f3",
                            "e5", "d4", "c3",
                            "e6"),
                     board, "f6");
    }

    @Test
    void bishop1() {
        // three bishops on each color of square in bottom corner of board (starting position), partially blocked by pawn walls
        Board board = new Board(List.of("Bf1", "Bf2", "Bf3", "Pc4", "Pc5", "Pc6", "Pi4", "Pi5", "Pi6"));
        assertMovesEqual(Set.of("e2", "d3", "g2", "h3"), board, "f1");
        assertMovesEqual(Set.of("e3", "d4", "g3", "h4", "d1", "h1"), board, "f2");
        assertMovesEqual(Set.of("e4", "d5", "g4", "h5", "d2", "b1", "h2", "j1", "e1", "g1"), board, "f3");
    }

    @Test
    void bishop2() {
        // bishop in centre of board, blocked in all rook-based directions; verify that it can still sneak
        // past
        Board board = new Board(List.of("Bf6", "Pf5", "Pg5", "Pe5", "Pg6", "Pe6", "Pf7"));
        assertMovesEqual(Set.of("e7", "d8",
                            "g7", "h8", 
                            "e4", "d2", 
                            "g4", "h2", 
                            "d5", "b4", 
                            "h5", "j4"),
                     board, "f6");
    }
    
    @Test
    void bishop3() {
        // checking that a black bishop also works correctly
        Board board = new Board(List.of("ba6"));
        assertMovesEqual(Set.of("c7", "e8", "g8", "i7", "k6",
                                "b5", "c4", "d3", "e2", "f1"), board, "a6");
    }

    @Test
    void queen() {
        // the queen has godlike reach so this test will be really hard to simplify no matter what
        Board board = new Board(List.of("Qa6", "qk6", "pe8", "Pe6"));

        assertMovesEqual(Set.of(
                "a5", "a4", "a3", "a2", "a1",
                "b5", "c4", "d3", "e2", "f1",
                "b6", "c6", "d6",
                "c7", "e8",
                "b7", "c8", "d9", "e10", "f11"
        ), board, "a6");

        assertMovesEqual(Set.of(
                "k5", "k4", "k3", "k2", "k1",
                "j5", "i4", "h3", "g2", "f1",
                "j6", "i6", "h6", "g6", "f6", "e5", "d4", "c3", "b2", "a1",
                "i7", "g8",
                "j7", "i8", "h9", "g10", "f11"
        ), board, "k6");
    }

    @Test
    void kingSimple() {
        Board board = new Board(List.of("Kf6", "Pg6", "Pe7", "Pe6"));

        assertMovesEqual(Set.of("f5", "e4", "e5", "d5", "f7", "g7", "h5", "g5", "g4"), board, "f6");
    }

    @Test
    void kingNoMovesIntoCheck1() {
        Board board = new Board(List.of("Ke5", "qc6"));

        assertMovesEqual(Set.of("f7", "f5", "f4", "d4", "d3"), board, "e5");
    }

    @Test
    void kingNoMovesIntoCheck2() {
        Board board = new Board(List.of("ke5", "Pe6", "Pg4", "Ph4", "Pd4"));

        assertMovesEqual(Set.of("d3", "d5", "f4", "f6", "e4", "d4", "e6"), board, "e5");
    }

    @Test
    void kingNoMovesIntoCheck3() {
        Board board = new Board(List.of("Ke5", "ke3", "nf8"));

        assertMovesEqual(Set.of("c4", "d6", "e6", "f6", "f7"), board, "e5");
    }

    @Test
    void kingNoMovesIntoCheck4() {
        Board board = new Board(List.of("kh8", "ph7", "Qj7", "Nh6", "Bi5", "Bd3"));

        assertMovesEqual(Set.of("g7"), board, "h8");
    }



    @Test
    void enPassant() {
        Board board = new Board(List.of("Pd3", "pe5"));
        board.applyMoveWithLegalityCheck(new Move("d3", "d5"), Piece.Color.WHITE);
        assertMovesEqual(Set.of("e4", "d4"), board, "e5");
    }

    @Test
    void enPassantNotPossibleAfter2Moves() {
        Board board = new Board(List.of("Pd3", "pe5", "pf7", "Pg4"));
        board.applyMoveWithLegalityCheck(new Move("d3", "d5"), Piece.Color.WHITE);
        board.applyMoveWithLegalityCheck(new Move("f7", "f6"), Piece.Color.BLACK);
        board.applyMoveWithLegalityCheck(new Move("g4", "g5"), Piece.Color.WHITE);
        assertMovesEqual(Set.of("e4"), board, "e5");
    }
}