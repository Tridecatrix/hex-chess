import model.Game;
import model.Move;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DrawTest {
    @Test
    void drawByRepetition() {
        Game game = new Game();
        game.applyMove(new Move("d1", "c3"));
        game.applyMove(new Move("d9", "c6"));
        game.applyMove(new Move("c3", "d1"));
        game.applyMove(new Move("c6", "d9"));
        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());

        game.applyMove(new Move("d1", "c3"));
        game.applyMove(new Move("d9", "c6"));
        game.applyMove(new Move("c3", "d1"));
        game.applyMove(new Move("c6", "d9"));
        assertTrue(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());

        game.applyMove(new Move("d1", "c3"));
        game.applyMove(new Move("d9", "c6"));
        game.applyMove(new Move("c3", "d1"));
        game.applyMove(new Move("c6", "d9"));
        assertTrue(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());

        game.applyMove(new Move("d1", "c3"));
        game.applyMove(new Move("d9", "c6"));
        game.applyMove(new Move("c3", "d1"));
        game.applyMove(new Move("c6", "d9"));
        assertTrue(game.claimDraw());
        assertTrue(game.checkIfForcedDraw());

        game.applyMove(new Move("d1", "c3"));
        game.applyMove(new Move("d9", "c6"));
        game.applyMove(new Move("c3", "d1"));
        game.applyMove(new Move("c6", "d9"));
        assertTrue(game.claimDraw());
        assertTrue(game.checkIfForcedDraw());
    }

    @Test
    void drawByMovesWithoutDevelopment() {
        Game game = new Game(List.of("ke10", "rg10", "Ke1", "Rg1", "pe9", "pd9", "Pd1", "Pe2"));
        System.out.println("Initial board:");
        System.out.println(game);

        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("g1", "f1"));
        game.applyMove(new Move("g10", "f11"));
        game.applyMove(new Move("f1", "f2"));
        game.applyMove(new Move("f11", "f10"));
        game.applyMove(new Move("f2", "f3"));
        game.applyMove(new Move("f10", "f9"));
        game.applyMove(new Move("f3", "e3"));
        game.applyMove(new Move("f9", "e8"));
        game.applyMove(new Move("e3", "c1"));
        game.applyMove(new Move("e8", "d8")); // 5

        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("c1", "b1"));
        game.applyMove(new Move("d8", "e8"));
        game.applyMove(new Move("b1", "a1"));
        game.applyMove(new Move("e8", "f9"));
        game.applyMove(new Move("a1", "a2"));
        game.applyMove(new Move("f9", "g9"));
        game.applyMove(new Move("a2", "b2"));
        game.applyMove(new Move("g9", "h9"));
        game.applyMove(new Move("b2", "c2"));
        game.applyMove(new Move("h9", "i8")); // 10

        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("c2", "d2"));
        game.applyMove(new Move("i8", "h8"));
        game.applyMove(new Move("d2", "e3"));
        game.applyMove(new Move("h8", "g8"));
        game.applyMove(new Move("e3", "f3"));
        game.applyMove(new Move("g8", "f8"));
        game.applyMove(new Move("f3", "g2"));
        game.applyMove(new Move("f8", "e7"));
        game.applyMove(new Move("g2", "h1"));
        game.applyMove(new Move("e7", "d7")); // 15

        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("h1", "i1"));
        game.applyMove(new Move("d7", "c7"));
        game.applyMove(new Move("i1", "j1"));
        game.applyMove(new Move("c7", "c8"));
        game.applyMove(new Move("j1", "k1"));
        game.applyMove(new Move("c8", "b7"));
        game.applyMove(new Move("k1", "j2"));
        game.applyMove(new Move("b7", "b6"));
        game.applyMove(new Move("j2", "i2"));
        game.applyMove(new Move("b6", "c6")); // 20

        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("i2", "h2"));
        game.applyMove(new Move("c6", "d6"));
        game.applyMove(new Move("h2", "g3"));
        game.applyMove(new Move("d6", "e6"));
        game.applyMove(new Move("g3", "f4"));
        game.applyMove(new Move("e6", "f7"));
        game.applyMove(new Move("f4", "e4"));
        game.applyMove(new Move("f7", "g7"));
        game.applyMove(new Move("e4", "d3"));
        game.applyMove(new Move("g7", "h7")); // 25

        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("d3", "c3"));
        game.applyMove(new Move("h7", "i7"));
        game.applyMove(new Move("c3", "b3"));
        game.applyMove(new Move("i7", "j7"));
        game.applyMove(new Move("b3", "a3"));
        game.applyMove(new Move("j7", "k6"));
        game.applyMove(new Move("a3", "a4"));
        game.applyMove(new Move("k6", "j6"));
        game.applyMove(new Move("a4", "b4"));
        game.applyMove(new Move("j6", "i6")); // 30

        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("b4", "c4"));
        game.applyMove(new Move("i6", "h6"));
        game.applyMove(new Move("c4", "d4"));
        game.applyMove(new Move("h6", "g6"));
        game.applyMove(new Move("d4", "e4"));
        game.applyMove(new Move("g6", "f6"));
        game.applyMove(new Move("e4", "f5"));
        game.applyMove(new Move("f6", "e5"));
        game.applyMove(new Move("f5", "g4"));
        game.applyMove(new Move("e5", "d5")); // 35

        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("g4", "h4"));
        game.applyMove(new Move("d5", "c5"));
        game.applyMove(new Move("h4", "i4"));
        game.applyMove(new Move("c5", "b5"));
        game.applyMove(new Move("i4", "j4"));
        game.applyMove(new Move("b5", "a5"));
        game.applyMove(new Move("j4", "k4"));
        game.applyMove(new Move("a5", "a4"));
        game.applyMove(new Move("k4", "j4"));
        game.applyMove(new Move("a4", "a5")); // 40

        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("j4", "k4"));
        game.applyMove(new Move("a5", "a4"));
        game.applyMove(new Move("k4", "j4"));
        game.applyMove(new Move("a4", "a5"));
        game.applyMove(new Move("j4", "i4"));
        game.applyMove(new Move("a5", "b5"));
        game.applyMove(new Move("i4", "h4"));
        game.applyMove(new Move("b5", "c5"));
        game.applyMove(new Move("h4", "g4"));
        game.applyMove(new Move("c5", "d5")); // 45

        assertFalse(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("g4", "f5"));
        game.applyMove(new Move("d5", "e5"));
        game.applyMove(new Move("f5", "e4"));
        game.applyMove(new Move("e5", "f6"));
        game.applyMove(new Move("e4", "d4"));
        game.applyMove(new Move("f6", "g6"));
        game.applyMove(new Move("d4", "c4"));
        game.applyMove(new Move("g6", "h6"));
        game.applyMove(new Move("c4", "b4"));
        game.applyMove(new Move("h6", "i6")); // 50

        assertTrue(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("b4", "a4"));
        game.applyMove(new Move("i6", "j6"));
        game.applyMove(new Move("a4", "a3"));
        game.applyMove(new Move("j6", "k6"));
        game.applyMove(new Move("a3", "b3"));
        game.applyMove(new Move("k6", "j7"));
        game.applyMove(new Move("b3", "c3"));
        game.applyMove(new Move("j7", "i7"));
        game.applyMove(new Move("c3", "d3"));
        game.applyMove(new Move("i7", "h7")); // 55

        assertTrue(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("d3", "e4"));
        game.applyMove(new Move("h7", "g7"));
        game.applyMove(new Move("e4", "f4"));
        game.applyMove(new Move("g7", "f7"));
        game.applyMove(new Move("f4", "g3"));
        game.applyMove(new Move("f7", "e6"));
        game.applyMove(new Move("g3", "h2"));
        game.applyMove(new Move("e6", "d6"));
        game.applyMove(new Move("h2", "i2"));
        game.applyMove(new Move("d6", "c6")); // 60

        assertTrue(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("i2", "j2"));
        game.applyMove(new Move("c6", "b6"));
        game.applyMove(new Move("j2", "k1"));
        game.applyMove(new Move("b6", "b7"));
        game.applyMove(new Move("k1", "j1"));
        game.applyMove(new Move("b7", "c8"));
        game.applyMove(new Move("j1", "i1"));
        game.applyMove(new Move("c8", "c7"));
        game.applyMove(new Move("i1", "h1"));
        game.applyMove(new Move("c7", "d7")); // 65

        assertTrue(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("h1", "g2"));
        game.applyMove(new Move("d7", "e7"));
        game.applyMove(new Move("g2", "f3"));
        game.applyMove(new Move("e7", "f8"));
        game.applyMove(new Move("f3", "e3"));
        game.applyMove(new Move("f8", "g8"));
        game.applyMove(new Move("e3", "d2"));
        game.applyMove(new Move("g8", "h8"));
        game.applyMove(new Move("d2", "c2"));
        game.applyMove(new Move("h8", "i8")); // 70

        assertTrue(game.claimDraw());
        assertFalse(game.checkIfForcedDraw());
        game.applyMove(new Move("c2", "b2"));
        game.applyMove(new Move("i8", "h9"));
        game.applyMove(new Move("b2", "a2"));
        game.applyMove(new Move("h9", "g9"));
        game.applyMove(new Move("a2", "a1"));
        game.applyMove(new Move("g9", "f9"));
        game.applyMove(new Move("a1", "b1"));
        game.applyMove(new Move("f9", "e8"));
        game.applyMove(new Move("b1", "c1"));
        game.applyMove(new Move("e8", "d8")); // 75
        assertTrue(game.claimDraw());
        assertTrue(game.checkIfForcedDraw());

    }
}
