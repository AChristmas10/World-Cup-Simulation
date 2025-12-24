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
        setBorder(BorderFactory.createTitledBorder(groupName));

        String[] columns = { "Team", "Wins", "Draws", "Losses", "Points" };
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(18);
        table.setPreferredScrollableViewportSize(new Dimension(200, 100));

        add(new JScrollPane(table), BorderLayout.CENTER);
        setPreferredSize(new Dimension(200, 140));
        setMaximumSize(new Dimension(200, 140));

        refresh();
    }

    public void refresh() {
        model.setRowCount(0);
        ArrayList<Team> teams = group.getTeams();
        for (Team t : teams) {
            model.addRow(new Object[] {
                    t.getName(),
                    t.getWins(),
                    t.getDraws(),
                    t.getLosses(),
                    t.getPoints()
            });
        }
    }
}
