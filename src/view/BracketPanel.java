package view;

import manager.Match;
import manager.Team;
import manager.Tournament;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BracketPanel extends JPanel {

    private Tournament tournament;
    private ArrayList<ArrayList<Match>> rounds; // store matches per round

    public BracketPanel(Tournament tournament) {
        this.tournament = tournament;
        this.rounds = new ArrayList<>();
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        generateRounds();
        drawBracket();
    }

    private void generateRounds() {
        // Round 1: top32Teams
        ArrayList<Team> roundTeams = new ArrayList<>(tournament.top32Teams); // assume top32Teams is accessible
        ArrayList<Match> firstRound = new ArrayList<>();

        for (int i = 0; i < roundTeams.size(); i += 2) {
            firstRound.add(new Match(roundTeams.get(i), roundTeams.get(i + 1)));
        }

        rounds.add(firstRound);

        // Other rounds: 16, 8, 4, 2
        int matchesCount = firstRound.size() / 2;
        while (matchesCount >= 1) {
            ArrayList<Match> round = new ArrayList<>();
            for (int i = 0; i < matchesCount; i++) {
                round.add(new Match(new Team("TBD", 0, 0, 0, 0, 0),
                        new Team("TBD", 0, 0, 0, 0, 0)));
            }
            rounds.add(round);
            matchesCount /= 2;
        }
    }

    private void drawBracket() {
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);

        for (int col = 0; col < rounds.size(); col++) {
            ArrayList<Match> round = rounds.get(col);

            gbc.gridx = col;
            int row = 0;

            for (Match m : round) {
                JPanel matchPanel = new JPanel();
                matchPanel.setLayout(new GridLayout(2, 1));
                matchPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                matchPanel.setBackground(Color.LIGHT_GRAY);

                JLabel teamALabel = new JLabel(m.getTeamA().getName(), SwingConstants.CENTER);
                JLabel teamBLabel = new JLabel(m.getTeamB().getName(), SwingConstants.CENTER);

                matchPanel.add(teamALabel);
                matchPanel.add(teamBLabel);

                gbc.gridy = row;
                add(matchPanel, gbc);

                row += (int) Math.pow(2, col); // space matches vertically
            }
        }

        revalidate();
        repaint();
    }

    // Call this after playing a round to update winners
    public void updateRound(int roundIndex) {
        ArrayList<Match> currentRound = rounds.get(roundIndex);
        ArrayList<Match> nextRound = rounds.get(roundIndex + 1);

        for (int i = 0; i < nextRound.size(); i++) {
            Team winner1 = currentRound.get(i * 2).playKnockout();
            Team winner2 = currentRound.get(i * 2 + 1).playKnockout();

            nextRound.get(i).getTeamA().setStrength(winner1.getStrength());
            nextRound.get(i).getTeamA().setStrength(winner1.getStrength());
            nextRound.get(i).getTeamB().setStrength(winner2.getStrength());
        }

        drawBracket();
    }
}
