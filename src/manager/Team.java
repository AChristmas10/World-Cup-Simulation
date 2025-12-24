package manager;

public class Team {
    private String name;
    private int strength;
    private int wins;
    private int losses;
    private int draws;
    private int points;

    public Team(String name, int strength, int wins, int losses, int draws, int points) {
        this.name = name;
        this.strength = strength;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
        this.points = 0;
    }

    public String getName() {
        return name;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getDraws() {
        return draws;
    }

    public int getPoints() {
        return points;
    }

    public void addWin() {
        wins++;
        points += 3;
    }

    public void addLoss() {
        losses++;
    }

    public void addDraw() {
        draws++;
        points += 1;
    }

    public void getStats() {
        System.out.println(getName() + "," + getWins() + "," + getLosses() + "," + getDraws() + "," + getPoints());
    }

    public void setName(String name) {
        this.name = name;
    }
}
