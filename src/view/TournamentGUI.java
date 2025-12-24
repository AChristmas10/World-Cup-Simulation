package view;

import manager.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class TournamentGUI extends JFrame {

    private Tournament tournament;
    private TeamManager manager;

    // Group stage controls
    private JButton loadButton, simulateButton;
    private JComboBox<String> modeCombo;
    private JFileChooser fileChooser;
    private JPanel groupsPanel;
    private boolean simulationStarted = false;
    private boolean groupStageComplete = false;

    private enum SimMode { ONE_GAME, ONE_GAME_ALL_GROUPS, WHOLE_GROUP_STAGE }
    private SimMode simMode = SimMode.ONE_GAME;
    private int[] groupMatchIndices;

    // Knockout controls
    private KnockoutPanel knockoutPanel;
    private JButton nextMatchBtn, playRoundBtn, playTournamentBtn;
    private JPanel knockoutControlPanel;

    // Button to transition to knockout
    private JButton goToKnockoutBtn;
    private JPanel topPanel;

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
        simulateButton = new JButton("Simulate");

        String[] options = { "One Game at a Time", "One Game for All Groups", "Whole Group Stage" };
        modeCombo = new JComboBox<>(options);
        modeCombo.addActionListener(e -> {
            if (!simulationStarted) {
                int idx = modeCombo.getSelectedIndex();
                switch (idx) {
                    case 0 -> simMode = SimMode.ONE_GAME;
                    case 1 -> simMode = SimMode.ONE_GAME_ALL_GROUPS;
                    case 2 -> simMode = SimMode.WHOLE_GROUP_STAGE;
                }
            }
        });

        topPanel.add(loadButton);
        topPanel.add(modeCombo);
        topPanel.add(simulateButton);
        add(topPanel, BorderLayout.NORTH);

        groupsPanel = new JPanel();
        groupsPanel.setLayout(new GridLayout(0, 3, 5, 5));
        add(new JScrollPane(groupsPanel), BorderLayout.CENTER);

        fileChooser = new JFileChooser();

        loadButton.addActionListener(e -> loadTeams());
        simulateButton.addActionListener(e -> simulate());
    }

    private void loadTeams() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                manager = new TeamManager();
                manager.loadTeamsFromUserFile(file);

                tournament = new Tournament(manager.getTeams());
                tournament.createGroups();

                groupMatchIndices = new int[tournament.getGroups().size()];

                groupsPanel.removeAll();
                char groupLabel = 'A';
                for (Group g : tournament.getGroups()) {
                    String groupName = "Group " + groupLabel;
                    groupsPanel.add(new GroupPanel(g, groupName));
                    groupLabel++;
                }

                groupsPanel.revalidate();
                groupsPanel.repaint();
                simulationStarted = false;

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading teams: " + ex.getMessage());
            }
        }
    }

    private void simulate() {
        if (tournament == null) return;

        simulationStarted = true;
        modeCombo.setEnabled(false);

        boolean allDone = true;

        switch (simMode) {
            case ONE_GAME -> allDone = simulateOneGame();
            case ONE_GAME_ALL_GROUPS -> allDone = simulateOneGameAllGroups();
            case WHOLE_GROUP_STAGE -> allDone = simulateWholeGroupStage();
        }

        if (allDone) {
            JOptionPane.showMessageDialog(this, "Group Stage Complete!");
            groupStageComplete = true;

            // Hide group stage buttons
            loadButton.setVisible(false);
            simulateButton.setVisible(false);
            modeCombo.setVisible(false);

            // Show "Go to Knockout Stage" button
            goToKnockoutBtn = new JButton("Go to Knockout Stage");
            topPanel.add(goToKnockoutBtn);
            goToKnockoutBtn.addActionListener(e -> {
                remove(groupsPanel);
                remove(topPanel);
                showKnockoutControls();
                revalidate();
                repaint();
            });

            revalidate();
            repaint();
        }
    }

    private boolean simulateOneGame() {
        boolean anyLeft = false;

        for (int i = 0; i < tournament.getGroups().size(); i++) {
            Group g = tournament.getGroups().get(i);
            var matches = g.getAllMatches();
            int idx = groupMatchIndices[i];

            if (idx < matches.size()) {
                Team teamA = matches.get(idx).getTeamA();
                Team teamB = matches.get(idx).getTeamB();

                g.playMatch(teamA, teamB);
                ((GroupPanel) groupsPanel.getComponent(i)).refresh();
                groupMatchIndices[i]++;
                anyLeft = true;
                break;
            }
        }
        return !anyLeft;
    }

    private boolean simulateOneGameAllGroups() {
        boolean anyLeft = false;

        for (int i = 0; i < tournament.getGroups().size(); i++) {
            Group g = tournament.getGroups().get(i);
            ArrayList<Team> teams = g.getTeams();
            int totalMatches = teams.size() * (teams.size() - 1) / 2;

            if (groupMatchIndices[i] < totalMatches) {
                int idx = groupMatchIndices[i];
                int teamAIndex = 0, teamBIndex = 0;
                int counter = 0;
                outer:
                for (int a = 0; a < teams.size(); a++) {
                    for (int b = a + 1; b < teams.size(); b++) {
                        if (counter == idx) {
                            teamAIndex = a;
                            teamBIndex = b;
                            break outer;
                        }
                        counter++;
                    }
                }

                g.playMatch(teams.get(teamAIndex), teams.get(teamBIndex));
                ((GroupPanel) groupsPanel.getComponent(i)).refresh();
                groupMatchIndices[i]++;
                anyLeft = true;
            }
        }
        return !anyLeft;
    }

    private boolean simulateWholeGroupStage() {
        boolean anyLeft = false;

        for (int i = 0; i < tournament.getGroups().size(); i++) {
            Group g = tournament.getGroups().get(i);
            var matches = g.getAllMatches();

            for (int j = groupMatchIndices[i]; j < matches.size(); j++) {
                Team teamA = matches.get(j).getTeamA();
                Team teamB = matches.get(j).getTeamB();
                g.playMatch(teamA, teamB);
                anyLeft = true;
            }

            ((GroupPanel) groupsPanel.getComponent(i)).refresh();
            groupMatchIndices[i] = matches.size();
        }
        return !anyLeft;
    }

    private void showKnockoutControls() {
        tournament.determineTop32();

        knockoutPanel = new KnockoutPanel(tournament);
        add(knockoutPanel, BorderLayout.CENTER);

        // Highlight first match immediately
        knockoutPanel.currentRound = 0;
        knockoutPanel.currentMatch = 0;
        knockoutPanel.repaint();

        knockoutControlPanel = new JPanel();
        nextMatchBtn = new JButton("Next Match");
        playRoundBtn = new JButton("Play Round");
        playTournamentBtn = new JButton("Play Tournament");

        knockoutControlPanel.add(nextMatchBtn);
        knockoutControlPanel.add(playRoundBtn);
        knockoutControlPanel.add(playTournamentBtn);
        add(knockoutControlPanel, BorderLayout.NORTH);

        nextMatchBtn.addActionListener(e -> knockoutPanel.playNextMatch());
        playRoundBtn.addActionListener(e -> knockoutPanel.playFullRoundOfCurrent());
        playTournamentBtn.addActionListener(e -> knockoutPanel.playFullTournament());

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
