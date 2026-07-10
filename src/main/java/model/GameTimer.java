package model;

import model.piece.Piece;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameTimer {
    // static timing parameters
    Map<Piece.Color, Integer> incrementSec = new HashMap<>();
    Map<Piece.Color, Integer> startingTimeSec = new HashMap<>();

    // dynamic timing parameters
    Map<Piece.Color, Integer> timeRemainingMs = new HashMap<>();
    long lastTimeUpdatedNs = -1;

    boolean enabled;

    public Map<Piece.Color, Integer> getIncrementSec() {
        return incrementSec;
    }

    public Map<Piece.Color, Integer> getStartingTimeSec() {
        return startingTimeSec;
    }

    public enum TimingSetting {
        BULLET(1, 0),
        BLITZ(3, 2),
        RAPID(15, 10),
        CLASSICAL(60, 30);

        int timeMin;
        int incrementSec;

        TimingSetting(int timeMin, int incrementSec) {
            this.timeMin = timeMin;
            this.incrementSec = incrementSec;
        }
    }

    public GameTimer(TimingSetting setting, List<Piece.Color> activeColors) {
        for (Piece.Color color : activeColors) {
            this.incrementSec.put(color, setting.incrementSec);
            this.startingTimeSec.put(color, setting.timeMin * 60);
        }
    }

    public String getTimeRemainingAsString(Piece.Color playerColor) {
        int timeRemainingMillis = this.timeRemainingMs.get(playerColor);

        Duration duration = Duration.ofMillis(timeRemainingMillis);
        long minutes = duration.toMinutes();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();

        if (duration.toSeconds() > 20)
            return String.format("%02d:%02d", minutes, seconds);
        else {
            return String.format("%02d:%02d.%03d", minutes, seconds, millis);
        }
    }

    // constructor for custom timing
    public GameTimer(Map<Piece.Color, Integer> startingTimeSec, Map<Piece.Color, Integer> incrementSec) {
        this.startingTimeSec = startingTimeSec;
        this.incrementSec = incrementSec;
    }

    public void startOrRestartTimer() {
        this.enabled = true;
        lastTimeUpdatedNs = System.nanoTime();
        for (Piece.Color color : startingTimeSec.keySet()) {
            timeRemainingMs.put(color, startingTimeSec.get(color) * 1000);
        }
    }

    public void updateTimers(long now, Piece.Color currentPlayer) {
        if (enabled) {
            if (lastTimeUpdatedNs < 0) {
                lastTimeUpdatedNs = now;
                return;
            }

            long timeElapsedMillis = (now - this.lastTimeUpdatedNs) / 1000000;
            lastTimeUpdatedNs = now;

            // note: slight inaccuracy if the player changed since the last time timeUpdatedNs was taken, but
            // updateTimers should be called often enough that this doesn't matter
            timeRemainingMs.put(currentPlayer, Math.max(timeRemainingMs.get(currentPlayer) - (int) timeElapsedMillis, 0));
        }
    }

    public void updateTimersPlayerChange(Piece.Color previousPlayer, Piece.Color nextPlayer) {
        if (enabled) {
            long now = System.nanoTime();
            this.updateTimers(now, previousPlayer); // handle time elapsed since last update

            // add the increment to the next player's time
            timeRemainingMs.put(nextPlayer, timeRemainingMs.get(nextPlayer) + incrementSec.get(nextPlayer) * 1000);
        }
    }

    public boolean isOutOfTime(Piece.Color currentPlayer) {
        return timeRemainingMs.get(currentPlayer) == 0;
    }

    public void stopTimer() {
        this.enabled = false;
    }
}
