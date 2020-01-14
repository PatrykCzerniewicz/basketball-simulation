public class Team implements Comparable<Team> {

    protected String name, city, conference, division;
    protected Player[] players = new Player[5];
    protected Player lastPassFrom;
    protected boolean isInPossession;
    protected int score;
    protected int wins, losses;
    private int playoffSeed, seriesWins, seriesLosses;

    public Team() {}

    public Team(String city, String name, String conference, String division) {
        this.city = city;
        this.name = name;
        this.conference = conference;
        this.division = division;
        score = 0;
    }

    public void addPlayer(int pos, Player p) {
        players[pos] = p;
        p.setPosition(pos);
    }

    public Player getPlayer(int pos) { return players[pos]; }

    public void setPos() { this.isInPossession = true; }
    public void setNoPos() {
        this.isInPossession = false;
        for(Player p : players)
            p.setNoBall();
    }

    public boolean getPos() { return this.isInPossession; }

    public Player getLastPassFrom() {return lastPassFrom;}
    public Player hasBall() {
        for(Player p : players) {
            if(p.getBall())
                return p;
        }
        return null;
    }

    public void printInfo() {
        String result = "";
        result += String.format("%s %s%n", city, name);
        result += "--------\n";
        result += String.format("Point Guard: %s%n", players[0]);
        result += String.format("Shooting Guard: %s%n", players[1]);
        result += String.format("Small Forward: %s%n", players[2]);
        result += String.format("Power Forward: %s%n", players[3]);
        result += String.format("Center: %s%n", players[4]);
        System.out.println(result);
    }

    public void printLineup() throws InterruptedException {
        int delay = 333;
        System.out.println(String.format("For the %s %s:", city, name)); Thread.sleep(delay);
        System.out.println(String.format(" at Point Guard, standing at %s, number %s, from %s: %s", players[0].inchesToFeet(), players[0].getNumber(), players[0].getFrom(), players[0])); Thread.sleep(delay);
        System.out.println(String.format(" at Shooting Guard, standing at %s, number %s, from %s: %s", players[1].inchesToFeet(), players[1].getNumber(), players[1].getFrom(), players[1])); Thread.sleep(delay);
        System.out.println(String.format(" at Small Forward, standing at %s, number %s, from %s: %s", players[2].inchesToFeet(), players[2].getNumber(), players[2].getFrom(), players[2])); Thread.sleep(delay);
        System.out.println(String.format(" at Power Forward, standing at %s, number %s, from %s: %s", players[3].inchesToFeet(), players[3].getNumber(), players[3].getFrom(), players[3])); Thread.sleep(delay);
        System.out.println(String.format(" and at Center, standing at %s, number %s, from %s: %s%n", players[4].inchesToFeet(), players[4].getNumber(), players[4].getFrom(), players[4])); Thread.sleep(delay);
    }

    public void printTeamStats() {
        System.out.println("------------------");
        System.out.println(this + ": Team Totals");
        System.out.printf("-- Points: %d | Rebounds: %d | Assists: %d | Steals: %d%n", getScore(), getTotalStat("rebs"), getTotalStat("asts"), getTotalStat("stls"));
        System.out.printf("-- Field Goals: %d/%d - %d%% | 3-point Field Goals: %d/%d - %d%%%n", getTotalStat("fgm"), getTotalStat("fga"), percentage(getTotalStat("fgm"), getTotalStat("fga")), getTotalStat("3pm"), getTotalStat("3pa"), percentage(getTotalStat("3pm"), getTotalStat("3pa")));
    }

    @Override
    public String toString() {
        return city + " " + name;
    }
    public String toStringPlayoffs() {
        return String.format("(%d) %s", playoffSeed, this.toString());
    }

    public String shortName() {
        String shortName = "";
        String[] spl = city.split(" ");
        if(spl.length == 1)
            shortName = city.substring(0, 3);
        else
            shortName = "" + spl[0].charAt(0) + spl[1].charAt(0) + name.charAt(0);

        //special cases
        if(name.equals("Nets"))
            shortName = "BKN";
        if(name.equals("Thunder"))
            shortName = "OKC";
        if(name.equals("Suns"))
            shortName = "PHX";

        return shortName.toUpperCase();
    }

    public int getScore() {
        score = players[0].getPts() + players[1].getPts() + players[2].getPts() + players[3].getPts() + players[4].getPts();
        return score;
    }

    public String getCity() {
        return this.city;
    }
    public String getName() {
        return this.name;
    }
    public String getConference() {
        return this.conference;
    }
    public String getDivision() {
        return this.division;
    }

    public void addWin() { wins++; }
    public void addLoss() { losses++; }
    public void addSeriesWin() { seriesWins++; }
    public void addSeriesLoss() { seriesLosses++; }
    public void zeroSeries() { seriesWins = 0; seriesLosses = 0; }
    public void zeroAllGames() { wins = 0; losses = 0; zeroSeries(); }

    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getSeriesWins() { return seriesWins; }
    public int getSeriesLosses() { return seriesLosses; }

    public void setSeed(int seed) { this.playoffSeed = seed; }
    public int getSeed() { return playoffSeed; }

    public Player getLeader(int type) {
        // 0 - eval, 1 - points, 2 - rebs, 3 - assists, 4 - steals, 5 - fg%, 6 - 3p%, 7 - 3pm

        Player leader = players[0];
        switch(type) {
            case 0: {
                for(int i = 1; i < 5; i++)
                    if(players[i].calculateSeasonEval() > leader.calculateSeasonEval())
                        leader = players[i];
                break;
            }
            case 1: {
                for(int i = 1; i < 5; i++)
                    if(players[i].getSeasonPts() > leader.getSeasonPts())
                        leader = players[i];
                break;
            }
            case 2: {
                for(int i = 1; i < 5; i++)
                    if(players[i].getSeasonRebs() > leader.getSeasonRebs())
                        leader = players[i];
                break;
            }
            case 3: {
                for(int i = 1; i < 5; i++)
                    if(players[i].getSeasonAsts() > leader.getSeasonAsts())
                        leader = players[i];
                break;
            }
            case 4: {
                for(int i = 1; i < 5; i++)
                    if(players[i].getSeasonStls() > leader.getSeasonStls())
                        leader = players[i];
                break;
            }
            case 5: {
                for(int i = 1; i < 5; i++)
                    if(players[i].getSeasonFGpct() > leader.getSeasonFGpct() || (players[i].getSeasonFGpct() == leader.getSeasonFGpct() && players[i].getSeasonFGM() > leader.getSeasonFGM()))
                        leader = players[i];
                break;
            }
            case 6: {
                for(int i = 1; i < 5; i++)
                    if(players[i].getSeason3Ppct() > leader.getSeason3Ppct() || (players[i].getSeason3Ppct() == leader.getSeason3Ppct() && players[i].getSeason3PM() > leader.getSeason3PM()))
                        leader = players[i];
                break;
            }
            case 7: {
                for(int i = 1; i < 5; i++)
                    if(players[i].getSeason3PM() > leader.getSeason3PM())
                        leader = players[i];
                break;
            }
        }

        return leader;

    }

    private int getTotalStat(String stat) {
        int total = 0;
        switch(stat) {
            case "rebs":
                for (Player p : players) {
                    total += p.getRebs();
                }
                break;
            case "asts":
                for (Player p : players) {
                    total += p.getAsts();
                }
                break;
            case "stls":
                for (Player p : players) {
                    total += p.getStls();
                }
                break;
            case "fgm":
                for (Player p : players) {
                    total += p.getFGM();
                }
                break;
            case "fga":
                for (Player p : players) {
                    total += p.getFGA();
                }
                break;
            case "3pm":
                for (Player p : players) {
                    total += p.get3PM();
                }
                break;
            case "3pa":
                for (Player p : players) {
                    total += p.get3PA();
                }
                break;
        }

        return total;
    }

    private int percentage(int top, int bottom) {
        if(top == 0)
            return 0;
        else if(bottom != 0)
            return (top * 100) / bottom;
        else
            return 0;
    }

    public boolean contains(Player p) {
        for(Player player : players) {
            if(player.equals(p))
                return true;
        }
        return false;
    }

    public void setLastPassFrom(Player p) {
        this.lastPassFrom = p;
    }

    @Override
    public int compareTo(Team other) {
        if(this.city.compareTo(other.getCity()) < 0)
            return -1;
        else if(this.city.compareTo(other.getCity()) > 0)
            return 1;
        else {
            return Integer.compare(this.name.compareTo(other.getName()), 0);
        }
    }
}
