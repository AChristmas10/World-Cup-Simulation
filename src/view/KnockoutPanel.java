package view;

import manager.Match;
import manager.Team;
import manager.Tournament;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class KnockoutPanel extends JPanel {

    private Tournament tournament;
    private ArrayList<ArrayList<Match>> rounds;
    public int currentRound = 0;
    public int currentMatch = 0;
    private Team champion = null;

    private final int matchWidth = 120;
    private final int matchHeight = 30;
    private final int xPadding = 50;

    private int[][] matchY;

    public KnockoutPanel(Tournament tournament) {
        this.tournament = tournament;
        this.rounds = new ArrayList<>();
        setBackground(Color.WHITE);
        setLayout(null);
        setPreferredSize(new Dimension(1200, 800));

        createRounds();
        repaint(); // Draw the bracket immediately
    }

    private void createRounds() {
        ArrayList<Team> top32 = new ArrayList<>(tournament.top32Teams);

        // Round of 32
        ArrayList<Match> round32 = new ArrayList<>();
        for (int i = 0; i < top32.size() / 2; i++) {
            round32.add(new Match(top32.get(i * 2), top32.get(i * 2 + 1)));
        }
        rounds.add(round32);

        int matches = round32.size() / 2;
        while (matches > 0) {
            ArrayList<Match> round = new ArrayList<>();
            for (int i = 0; i < matches; i++) {
                round.add(new Match(null, null)); // placeholders
            }
            rounds.add(round);
            matches /= 2;
        }

        matchY = new int[rounds.size()][];
        for (int r = 0; r < rounds.size(); r++) matchY[r] = new int[rounds.get(r).size()];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int totalRounds = rounds.size();

        for (int col = 0; col < totalRounds; col++) {
            ArrayList<Match> round = rounds.get(col);

            for (int i = 0; i < round.size(); i++) {
                Match m = round.get(i);

                // Determine x position
                int x;
                if (col == 0) x = (i < round.size() / 2) ? xPadding : width - xPadding - matchWidth;
                else if (col == totalRounds - 1) x = width / 2 - matchWidth / 2;
                else {
                    int branchWidth = (width / 2 - xPadding - matchWidth);
                    if (i < round.size() / 2) x = xPadding + col * branchWidth / (totalRounds - 1);
                    else x = width - xPadding - matchWidth - col * branchWidth / (totalRounds - 1);
                }

                // Determine y position
                int y;
                if (col == 0) {
                    int spacing = height / ((round.size() / 2) + 1);
                    int branchIndex = (i < round.size() / 2) ? i : i - round.size() / 2;
                    y = spacing * (branchIndex + 1);
                } else {
                    int parent1 = i * 2;
                    int parent2 = i * 2 + 1;
                    y = (matchY[col - 1][parent1] + matchY[col - 1][parent2]) / 2;
                }
                matchY[col][i] = y;

                // Highlight current match
                if (col == currentRound && i == currentMatch) {
                    g.setColor(Color.YELLOW);
                    g.fillRect(x - 5, y - 15, matchWidth + 10, matchHeight);
                }

                // Draw box
                g.setColor(Color.BLACK);
                g.drawRect(x, y - 15, matchWidth, matchHeight);

                // Draw team names only if available
                String teamAName = (m.getTeamA() != null) ? m.getTeamA().getName() : "TBD";
                String teamBName = (m.getTeamB() != null) ? m.getTeamB().getName() : "TBD";
                g.drawString(teamAName, x + 5, y - 2);
                g.drawString(teamBName, x + 5, y + 12);

                // Draw connecting lines only if next match exists and both teams are set
                if (col < totalRounds - 1) {
                    Match nextMatch = rounds.get(col + 1).get(i / 2);
                    if (nextMatch.getTeamA() != null || nextMatch.getTeamB() != null) {
                        int nextY = matchY[col + 1][i / 2] - 15 + matchHeight / 2;
                        int nextX;
                        if (i < round.size() / 2) nextX = xPadding + (col + 1) * (width / 2 - xPadding - matchWidth) / (totalRounds - 1);
                        else nextX = width - xPadding - matchWidth - (col + 1) * (width / 2 - xPadding - matchWidth) / (totalRounds - 1) + matchWidth;
                        g.drawLine((i < round.size() / 2) ? x + matchWidth : x, y - 15 + matchHeight / 2, nextX, nextY);
                    }
                }
            }
        }

        // Draw champion box only if champion is set
        if (champion != null) {
            int champX = width / 2 - matchWidth / 2;
            int champY = height / 2 - matchHeight / 2 + 200;
            g.drawRect(champX, champY, matchWidth, matchHeight);
            g.drawString(champion.getName(), champX + 5, champY + matchHeight / 2 + 5);
        }
    }


    public void playNextMatch() {
        if (currentRound >= rounds.size()) return;
        Match match = rounds.get(currentRound).get(currentMatch);
        if (match.getTeamA() == null || match.getTeamB() == null) return;

        Team winner = match.playKnockout();

        if (currentRound + 1 < rounds.size()) {
            Match nextMatch = rounds.get(currentRound + 1).get(currentMatch / 2);
            if (currentMatch % 2 == 0) nextMatch.setTeamA(winner);
            else nextMatch.setTeamB(winner);
        } else {
            champion = winner;
        }

        currentMatch++;
        if (currentMatch >= rounds.get(currentRound).size()) {
            currentMatch = 0;
            currentRound++;
        }

        repaint();
    }

    public void playFullRoundOfCurrent() {
        if (currentRound >= rounds.size()) return;
        int matchesInRound = rounds.get(currentRound).size();
        while (currentMatch < matchesInRound) playNextMatch();
    }

    public void playFullTournament() {
        while (currentRound < rounds.size()) playFullRoundOfCurrent();
    }
}
