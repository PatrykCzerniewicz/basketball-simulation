public class Game {

    private Team team1, team2;
    private String returnString;
    private Player potg;

    public Game(Team t1, Team t2) {
        this.team1 = t1;
        this.team2 = t2;
    }

    public Team getTeam(int n) {
        if(n == 1)
            return team1;
        else if(n == 2)
            return team2;
        else
            return null;
    }

    public Team getWinner() {
        if(team1.getScore() > team2.getScore())
            return team1;
        else
            return team2;
    }

    public void setPOTG(Player potg) { this.potg = potg; }

    public void printBox() {
        System.out.println(team1);
        for(int i = 0; i < 5; i++)
            team1.getPlayer(i).printInfo();
        System.out.println("------------------");
        System.out.println(team2);
        for(int i = 0; i < 5; i++)
            team2.getPlayer(i).printInfo();
    }

    public void setString(String s) {
        this.returnString = s;
    }

    @Override
    public String toString() {
        return returnString;
    }

    public String shortString() { return String.format("%d:%d", team1.getScore(), team2.getScore()); }

}
