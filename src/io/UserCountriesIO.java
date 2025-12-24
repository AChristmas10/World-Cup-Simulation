package io;

import manager.Team;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class UserCountriesIO {

    private ArrayList<String[]> validCountries = new ArrayList<>();

    public UserCountriesIO() throws FileNotFoundException {
        loadValidCountries();
    }

    private void loadValidCountries() throws FileNotFoundException {
        var input = getClass().getResourceAsStream("/valid_countries");
        if (input == null) {
            throw new FileNotFoundException("Could not find valid_countries in resources");
        }
        Scanner scanner = new Scanner(input);

        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split(",");
            validCountries.add(line);
        }
        scanner.close();
    }

    public ArrayList<String> readUserCountryNames(File file) throws FileNotFoundException {
        ArrayList<String> countries = new ArrayList<>();
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String country = scanner.nextLine();
            if (!country.isEmpty()) {
                countries.add(country);
            }
        }

        scanner.close();
        return countries;
    }

    public boolean teamValid(String country) throws FileNotFoundException {
        for (int i = 0; i < validCountries.size(); i++) {
            String[] entry = validCountries.get(i);
            if (entry[1].equalsIgnoreCase(country)) {
                return true;
            }
        }
        return false;
    }

    public int getRank(String country) throws FileNotFoundException {
        for (int i = 0; i < validCountries.size(); i++) {
            String[] entry = validCountries.get(i);
            if (entry[1].equalsIgnoreCase(country)) {
                return Integer.parseInt(entry[0]);
            }
        }
        return -1;
    }
}
