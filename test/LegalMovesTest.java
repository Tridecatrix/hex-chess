import static org.junit.jupiter.api.Assertions.assertEquals;

import model.Board;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class LegalMovesTest {
    Set<String> movesToString(Set<Board.Move> moves) {
        Set<String> strs = new HashSet<>(moves.size());
        for (Board.Move move : moves) {
            strs.add(move.toPos.toString());
        }
        return strs;
    }

    @Test
    void pawnTestStartingWhite() {
        Board board = new Board(List.of("Pf5"));
        System.out.println(board);
        assertEquals(Set.of("f6", "f7"), movesToString(board.getLegalMovesFromPos(new Board.Position("f5"))));
    }

    @Test
    void pawnTestStartingBlack() {
        Board board = new Board(List.of("pf7"));
        System.out.println(board);
        assertEquals(Set.of("f5", "f6"), movesToString(board.getLegalMovesFromPos(new Board.Position("f7"))));
    }

    @Test
    void pawnTestCapturesWhite() {
        Board board = new Board(List.of("Pc3", "qb3", "rd4"));
        System.out.println(board);
        assertEquals(Set.of("b3", "c4", "d4"), movesToString(board.getLegalMovesFromPos(new Board.Position("c3"))));
    }

    @Test
    void pawnTestCapturesBlack() {
        Board board = new Board(List.of("pg6", "Rh5", "Qf6"));
        System.out.println(board);
        assertEquals(Set.of("g5", "h5", "f6"), movesToString(board.getLegalMovesFromPos(new Board.Position("g6"))));
    }

    @Test
    void pawnTestPartialBlockedWhite() {
        Board board = new Board(List.of("Pf5", "Rg5"));
        System.out.println(board);
        assertEquals(Set.of("f6", "f7", "e5"), movesToString(board.getLegalMovesFromPos(new Board.Position("f5"))));
    }

    @Test
    void pawnTestPartialBlockedBlack() {
        Board board = new Board(List.of("pf7", "rg6"));
        System.out.println(board);
        assertEquals(Set.of("f6", "f5", "e6"), movesToString(board.getLegalMovesFromPos(new Board.Position("f7"))));
    }

    @Test
    void pawnTestAllBlockedWhite() {
        Board board = new Board(List.of("Pf5", "Rg5", "Qe5", "Bf6"));
        System.out.println(board);
        assertEquals(Set.of(), movesToString(board.getLegalMovesFromPos(new Board.Position("f5"))));
    }

    @Test
    void pawnTestAllBlockedBlack() {
        Board board = new Board(List.of("ph7", "ph6", "ng7", "bi6"));
        System.out.println(board);
        assertEquals(Set.of(), movesToString(board.getLegalMovesFromPos(new Board.Position("h7"))));
    }

    @Test
    void knightComplex1() {
        Board board = new Board(List.of("Nf6", "Pf7", "Pf5", "Pg5", "Pe5", "Pe6", "Pg6"));
        System.out.println(board);
        assertEquals(Set.of("e8", "g8",
                            "d7", "c5",
                            "h7", "i5",
                            "c4", "d3",
                            "i4", "h3",
                            "e3", "g3"),
                     movesToString(board.getLegalMovesFromPos(new Board.Position("f6"))));
    }

    @Test
    void knightComplex2() {
        Board board = new Board(List.of("nf6", "pf7", "pf5", "pg5", "pe5", "pe6", "pg6"));
        System.out.println(board);
        assertEquals(Set.of("e8", "g8",
                            "d7", "c5",
                            "h7", "i5",
                            "c4", "d3",
                            "i4", "h3",
                            "e3", "g3"),
                movesToString(board.getLegalMovesFromPos(new Board.Position("f6"))));
    }

    @Test
    void rookComplex() {
        Board board = new Board(List.of("Rf6", "pc3", "Pd6", "pf8", "Pj6", "Pf2"));
        System.out.println(board);
        assertEquals(Set.of("f7", "f8",
                            "g6", "h6", "i6",
                            "g5", "h4", "i3", "j2", "k1",
                            "f5", "f4", "f3",
                            "e5", "d4", "c3",
                            "e6", "d6", "c6"),
                     movesToString(board.getLegalMovesFromPos(new Board.Position("f6"))));
    }
}