package view;

import manager.Group;
import manager.Match;
import manager.Tournament;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GroupStagePanel extends JPanel {

    private Tournament tournament;
    private ArrayList<GroupPanel> panels = new ArrayList<>();

    private int groupIndex = 0;
    private int matchIndex = 0;

    public GroupStagePanel(Tournament tournament) {
        this.tournament = tournament;

        setLayout(new GridLayout(0, 4, 10, 10));
        setBackground(Color.WHITE);

        tournament.createGroups();

        char name = 'A';
        for (Group g : tournament.getGroups()) {
            GroupPanel gp = new GroupPanel(g, "Group " + name++);
            panels.add(gp);
            add(gp);
        }
    }

    public void playNextMatch() {
        if (groupIndex >= tournament.getGroups().size()) return;

        Group g = tournament.getGroups().get(groupIndex);
        ArrayList<Match> matches = g.getAllMatches();

        if (matchIndex < matches.size()) {
            Match m = matches.get(matchIndex);
            g.playMatch(m.getTeamA(), m.getTeamB());
            panels.get(groupIndex).refresh();
            matchIndex++;
        } else {
            matchIndex = 0;
            groupIndex++;
            playNextMatch();
        }
    }

    public void playFullRound() {
        for (int i = 0; i < tournament.getGroups().size(); i++) {
            Group g = tournament.getGroups().get(i);
            ArrayList<Match> matches = g.getAllMatches();

            if (matchIndex < matches.size()) {
                Match m = matches.get(matchIndex);
                g.playMatch(m.getTeamA(), m.getTeamB());
                panels.get(i).refresh();
            }
        }
        matchIndex++;
    }

    public void playAllMatches() {
        for (Group g : tournament.getGroups()) {
            for (Match m : g.getAllMatches()) {
                g.playMatch(m.getTeamA(), m.getTeamB());
            }
        }
        panels.forEach(GroupPanel::refresh);
    }
}
