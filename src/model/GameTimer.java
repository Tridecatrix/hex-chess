package model;

import model.piece.Piece;

import java.time.Duration;

public class GameTimer {
    // static timing parameters
    int incrementSecWhite;
    int incrementSecBlack;
    int startingTimeSecWhite;
    int startingTimeSecBlack;

    // dynamic timing parameters
    int whiteTimeRemainingMillis;
    int blackTimeRemainingMillis;
    long lastTimeUpdatedNs = -1;

    boolean enabled;

    public int getIncrementSecWhite() {
        return incrementSecWhite;
    }

    public int getIncrementSecBlack() {
        return incrementSecBlack;
    }

    public int getStartingTimeSecWhite() {
        return startingTimeSecWhite;
    }

    public int getStartingTimeSecBlack() {
        return startingTimeSecBlack;
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

    public GameTimer(TimingSetting setting) {
        this.incrementSecWhite = setting.incrementSec;
        this.incrementSecBlack = setting.incrementSec;
        this.startingTimeSecWhite = setting.timeMin * 60;
        this.startingTimeSecBlack = setting.timeMin * 60;
    }

    public String getTimeRemainingAsString(Piece.Color playerColor) {
        int timeRemainingMillis;
        if (playerColor == Piece.Color.WHITE) {
            timeRemainingMillis = whiteTimeRemainingMillis;
        } else {
            timeRemainingMillis = blackTimeRemainingMillis;
        }

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
    public GameTimer(int startingTimeSecWhite, int startingTimeSecBlack, int incrementSecWhite, int incrementSecBlack) {
        this.startingTimeSecWhite = startingTimeSecWhite;
        this.startingTimeSecBlack = startingTimeSecBlack;
        this.incrementSecWhite = incrementSecWhite;
        this.incrementSecBlack = incrementSecBlack;
    }

    public void startOrRestartTimer() {
        this.enabled = true;
        lastTimeUpdatedNs = System.nanoTime();
        this.whiteTimeRemainingMillis = startingTimeSecWhite * 1000;
        this.blackTimeRemainingMillis = startingTimeSecBlack * 1000;
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
            if (currentPlayer == Piece.Color.WHITE) {
                whiteTimeRemainingMillis -= (int) timeElapsedMillis;
                whiteTimeRemainingMillis = Math.max(whiteTimeRemainingMillis, 0);
            } else {
                blackTimeRemainingMillis -= (int) timeElapsedMillis;
                blackTimeRemainingMillis = Math.max(blackTimeRemainingMillis, 0);
            }
        }
    }

    public void updateTimersPlayerChange(Piece.Color previousPlayer, Piece.Color nextPlayer) {
        if (enabled) {
            long now = System.nanoTime();
            this.updateTimers(now, previousPlayer); // handle time elapsed since last update

            // add the increment to the next player's time
            if (nextPlayer == Piece.Color.WHITE) {
                whiteTimeRemainingMillis += this.incrementSecWhite * 1000;
            } else {
                blackTimeRemainingMillis += this.incrementSecBlack * 1000;
            }
        }
    }

    public boolean isOutOfTime(Piece.Color currentPlayer) {
        return currentPlayer == Piece.Color.WHITE ? whiteTimeRemainingMillis == 0 : blackTimeRemainingMillis == 0;
    }

    public void stopTimer() {
        this.enabled = false;
    }
}
