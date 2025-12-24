package view;

import manager.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class TournamentGUI extends JFrame {

    private Tournament tournament;
    private TeamManager manager;

    private JPanel topPanel;
    private JButton loadButton, simulateButton, playRoundBtn, fullStageBtn;
    private JFileChooser fileChooser;
    private JPanel groupsPanel;
    private boolean groupStageComplete = false;
    private int[] matchIndex;

    private KnockoutPanel knockoutPanel;

    public TournamentGUI() {
        setTitle("World Cup Simulator");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initGroupStageControls();
    }

    private void initGroupStageControls() {
        topPanel = new JPanel();
        loadButton = new JButton("Load Teams");
        simulateButton = new JButton("Simulate One Game");
        playRoundBtn = new JButton("Play Group Round");
        fullStageBtn = new JButton("Play Full Group Stage");

        topPanel.add(loadButton);
        topPanel.add(simulateButton);
        topPanel.add(playRoundBtn);
        topPanel.add(fullStageBtn);
        add(topPanel, BorderLayout.NORTH);

        groupsPanel = new JPanel(new GridLayout(3, 4, 8, 8)); // 4 groups per row
        add(new JScrollPane(groupsPanel), BorderLayout.CENTER);

        fileChooser = new JFileChooser();

        loadButton.addActionListener(e -> loadTeams());
        simulateButton.addActionListener(e -> simulateOneGame());
        playRoundBtn.addActionListener(e -> playGroupRound());
        fullStageBtn.addActionListener(e -> playFullGroupStage());
    }

    private void loadTeams() {
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            File file = fileChooser.getSelectedFile();
            manager = new TeamManager();
            manager.loadTeamsFromUserFile(file);

            tournament = new Tournament(manager.getTeams());
            tournament.createGroups();

            matchIndex = new int[tournament.getGroups().size()];

            groupsPanel.removeAll();
            char label = 'A';
            for (Group g : tournament.getGroups())
                groupsPanel.add(new GroupPanel(g, "Group " + label++));
            groupsPanel.revalidate();
            groupsPanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading teams: " + ex.getMessage());
        }
    }

    private void simulateOneGame() {
        boolean anyPlayed = false;
        for (int i = 0; i < tournament.getGroups().size(); i++) {
            Group g = tournament.getGroups().get(i);
            var matches = g.getAllMatches();
            if (matchIndex[i] < matches.size()) {
                Match m = matches.get(matchIndex[i]);
                g.playMatch(m.getTeamA(), m.getTeamB());
                ((GroupPanel) groupsPanel.getComponent(i)).refresh();
                matchIndex[i]++;
                anyPlayed = true;
                break;
            }
        }
        if (!anyPlayed) finishGroupStage();
    }

    private void playGroupRound() {
        boolean anyPlayed = false;
        for (int i = 0; i < tournament.getGroups().size(); i++) {
            Group g = tournament.getGroups().get(i);
            var matches = g.getAllMatches();
            if (matchIndex[i] < matches.size()) {
                Match m = matches.get(matchIndex[i]);
                g.playMatch(m.getTeamA(), m.getTeamB());
                ((GroupPanel) groupsPanel.getComponent(i)).refresh();
                matchIndex[i]++;
                anyPlayed = true;
            }
        }
        if (!anyPlayed) finishGroupStage();
    }

    private void playFullGroupStage() {
        boolean anyPlayed = false;
        for (int i = 0; i < tournament.getGroups().size(); i++) {
            Group g = tournament.getGroups().get(i);
            var matches = g.getAllMatches();
            for (; matchIndex[i] < matches.size(); matchIndex[i]++) {
                Match m = matches.get(matchIndex[i]);
                g.playMatch(m.getTeamA(), m.getTeamB());
            }
            ((GroupPanel) groupsPanel.getComponent(i)).refresh();
            anyPlayed = true;
        }
        if (anyPlayed) finishGroupStage();
    }

    private void finishGroupStage() {
        groupStageComplete = true;
        JOptionPane.showMessageDialog(this, "Group Stage Complete!");



        loadButton.setVisible(false);
        simulateButton.setVisible(false);
        playRoundBtn.setVisible(false);
        fullStageBtn.setVisible(false);

        showKnockoutStage();
    }

    private void showKnockoutStage() {
        tournament.determineTop32();
        knockoutPanel = new KnockoutPanel(tournament);
        add(knockoutPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton nextBtn = new JButton("Next Match");
        nextBtn.addActionListener(e -> knockoutPanel.playNextMatch());
        controlPanel.add(nextBtn);
        add(controlPanel, BorderLayout.NORTH);

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TournamentGUI gui = new TournamentGUI();
            gui.setVisible(true);
        });
    }
}
