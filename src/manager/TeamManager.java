package manager;

import io.UserCountriesIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class TeamManager {

    private ArrayList<Team> teams;
    private UserCountriesIO countryIO;

    public TeamManager() throws FileNotFoundException {
        teams = new ArrayList<>();
        countryIO = new UserCountriesIO();
    }

    // Load user teams from a file on disk
    public void loadTeamsFromUserFile(File file) throws FileNotFoundException {
        ArrayList<String> countryNames = countryIO.readUserCountryNames(file);

        for (String country : countryNames) {
            if (!countryIO.teamValid(country)) {
                throw new IllegalArgumentException("Invalid country: " + country);
            }

            int rank = countryIO.getRank(country);
            int strength = 3000 - (rank * 10);

            Team team = new Team(country, strength, 0, 0, 0, 0);
            teams.add(team);
        }
    }

    public ArrayList<Team> getTeams() {
        return new ArrayList<>(teams); // return a copy for safety
    }
}
