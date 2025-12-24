package manager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Group {

    private ArrayList<Team> teams;

    public Group(ArrayList<Team> teams) {
        if (teams.size() != 4) {
            throw new IllegalArgumentException("A group must contain exactly 4 teams.");
        }
        this.teams = teams;
    }

    public void playGroup() {
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                playMatch(teams.get(i), teams.get(j));
            }
        }
    }

    public ArrayList<Match> getAllMatches() {
        ArrayList<Match> matchList = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                matchList.add(new Match(teams.get(i), teams.get(j)));
            }
        }
        return matchList;
    }

    public void printGroupStandings(JTextArea area) {
        ArrayList<Team> sortedTeams = new ArrayList<>(teams);
        sortedTeams.sort((t1, t2) -> t2.getPoints() - t1.getPoints()); // sort descending
        for (Team t : sortedTeams) {
            area.append(t.getName() + " - Points: " + t.getPoints() + "\n");
        }
    }

    private final Random rand = new Random(); // single instance

    public void playMatch(Team teamA, Team teamB) {
        double drawChance = 0.25;
        double diffFactor = Math.abs(teamA.getStrength() - teamB.getStrength()) / 1000.0;
        drawChance = Math.max(0.05, drawChance - diffFactor);

        double roll = rand.nextDouble(); // use the single Random

        if (roll < drawChance) {
            teamA.addDraw();
            teamB.addDraw();
        } else {
            double totalStrength = teamA.getStrength() + teamB.getStrength();
            double winThreshold = teamA.getStrength() / totalStrength;

            if (rand.nextDouble() < winThreshold) {
                teamA.addWin();
                teamB.addLoss();
            } else {
                teamB.addWin();
                teamA.addLoss();
            }
        }
    }


    public ArrayList<Team> getTeamsSorted() {
        ArrayList<Team> sorted = new ArrayList<>(teams);
        Collections.sort(sorted, (t1, t2) -> {
            if (t2.getPoints() != t1.getPoints()) {
                return t2.getPoints() - t1.getPoints();
            }
            return t2.getStrength() - t1.getStrength();
        });
        return sorted;
    }

    public Team getFirstPlace() {
        return getTeamsSorted().get(0);
    }

    public Team getSecondPlace() {
        return getTeamsSorted().get(1);
    }

    public Team getThirdPlace() {
        return getTeamsSorted().get(2);
    }

    public ArrayList<Team> getTeams() {
        return new ArrayList<>(teams); // return copy
    }

}
