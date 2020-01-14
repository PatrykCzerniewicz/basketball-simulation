public class Record implements Comparable<Record> {

    private int type; // 0 - points, 1 - rebounds, 2 - assists, 3 - steals, 4 - threes
    private int value, year;
    private Player player;
    private Team team;

    public Record(Player player, int type, int value) {
        this.player = player;
        this.type = type;
        this.value = value;
        this.year = SimulateGame.year;
        this.team = player.getTeam();
    }

    public int getValue() { return this.value; }
    public int getType() { return this.type; }

    @Override
    public int compareTo(Record other) {
        return Integer.compare(this.value, other.getValue());
    }

    @Override
    public String toString() {
        return String.format(" %s (%s) - %d (%d)", player, team.shortName(), value, year);
    }

}
