package manager;

import java.util.Random;

public class Match {

    private Team teamA;
    private Team teamB;

    public Match(Team teamA, Team teamB) {
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public void play() {
        Random rand = new Random();
        int totalStrength = teamA.getStrength() + teamB.getStrength();
        int roll = rand.nextInt(totalStrength);

        if (roll < teamA.getStrength()) {
            teamA.addWin();
            teamB.addLoss();
        } else if (roll < totalStrength) {
            teamA.addLoss();
            teamB.addWin();
        }
    }

    public Team playKnockout() {
        Random rand = new Random();
        int totalStrength = teamA.getStrength() + teamB.getStrength();
        int roll = rand.nextInt(totalStrength);

        if (roll < teamA.getStrength()) {
            teamA.addWin();
            teamB.addLoss();
            return teamA;
        } else {
            teamB.addWin();
            teamA.addLoss();
            return teamB;
        }
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public void setTeamA(Team t) {
        this.teamA = t;
    }

    public void setTeamB(Team t) {
        this.teamB = t;
    }
}
