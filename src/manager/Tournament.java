package manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Tournament {

    private ArrayList<Team> allTeams;
    private ArrayList<Group> groups;
    public ArrayList<Team> top32Teams;

    public Tournament(ArrayList<Team> teams) {
        if (teams.size() != 48) {
            throw new IllegalArgumentException("Tournament requires exactly 48 teams.");
        }
        this.allTeams = new ArrayList<>(teams);
        this.groups = new ArrayList<>();
        this.top32Teams = new ArrayList<>();
    }

    public void createGroups() {
        Collections.shuffle(allTeams);
        groups.clear();

        for (int i = 0; i < 12; i++) {
            ArrayList<Team> groupTeams = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                groupTeams.add(allTeams.get(i * 4 + j));
            }
            groups.add(new Group(groupTeams));
        }
    }

    public void playGroupStage() {
        for (Group g : groups) {
            g.playGroup();
        }
    }

    public void determineTop32() {
        top32Teams.clear();
        ArrayList<Team> thirdPlaceTeams = new ArrayList<>();

        for (Group g : groups) {
            top32Teams.add(g.getFirstPlace());
            top32Teams.add(g.getSecondPlace());
            thirdPlaceTeams.add(g.getThirdPlace());
        }

        Collections.sort(thirdPlaceTeams, (t1, t2) -> {
            if (t2.getPoints() != t1.getPoints()) return t2.getPoints() - t1.getPoints();
            return t2.getStrength() - t1.getStrength();
        });

        for (int i = 0; i < 8; i++) {
            top32Teams.add(thirdPlaceTeams.get(i));
        }
    }

    public Team playKnockoutRounds() {
        ArrayList<Team> roundTeams = new ArrayList<>(top32Teams);
        Random rand = new Random();

        while (roundTeams.size() > 1) {
            ArrayList<Team> nextRound = new ArrayList<>();

            for (int i = 0; i < roundTeams.size(); i += 2) {
                Team teamA = roundTeams.get(i);
                Team teamB = roundTeams.get(i + 1);

                // Knockout match: no draw allowed
                Match match = new Match(teamA, teamB);
                Team winner = match.playKnockout();
                nextRound.add(winner);
            }

            roundTeams = nextRound;
        }

        return roundTeams.get(0); // Champion
    }

    public ArrayList<Group> getGroups() {
        return new ArrayList<>(groups);
    }
}
