package view;

import manager.Group;
import manager.Team;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class GroupPanel extends JPanel {
    private Group group;
    private JTable table;
    private DefaultTableModel model;

    public GroupPanel(Group group, String groupName) {
        this.group = group;
        setLayout(new BorderLayout());

        // Table columns
        String[] columns = {"Team", "Wins", "Losses", "Draws", "Points"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table read-only
            }
        };
        table = new JTable(model);

        // Reduce row height
        table.setRowHeight(20); // default is usually ~16-18, you can adjust

        // Initialize table with 0 stats
        for (Team t : group.getTeams()) {
            model.addRow(new Object[]{t.getName(), t.getWins(), t.getLosses(), t.getDraws(), t.getPoints()});
        }

        // Wrap table in scroll pane but reduce preferred size
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(200, 100)); // smaller height
        add(scrollPane, BorderLayout.CENTER);

        setBorder(BorderFactory.createTitledBorder(groupName));
    }

    // Update stats after a match
    public void refresh() {
        ArrayList<Team> teams = group.getTeams();
        for (int i = 0; i < teams.size(); i++) {
            Team t = teams.get(i);
            model.setValueAt(t.getWins(), i, 1);
            model.setValueAt(t.getLosses(), i, 2);
            model.setValueAt(t.getDraws(), i, 3);
            model.setValueAt(t.getPoints(), i, 4);
        }
    }
}
