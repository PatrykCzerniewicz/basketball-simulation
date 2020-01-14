import java.io.FileNotFoundException;
import java.util.*;

public class SimulateGame {

    private static ArrayList<Team> allTeams;
    public static int drafts = 0, year = 2020;
    private static ArrayList<Player> mvpTotal, allNBATotal;
    public static ArrayList<Record> scoringHighs = new ArrayList<>();
    public static ArrayList<Record> reboundingHighs = new ArrayList<>();
    public static ArrayList<Record> assistHighs = new ArrayList<>();
    public static ArrayList<Record> stealHighs = new ArrayList<>();
    public static ArrayList<Record> threesHighs = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        allTeams = Main.loadData();
        mvpTotal = new ArrayList<>();
        allNBATotal = new ArrayList<>();
        for(int i = 0; i < 50; i++)
            simSeason();
        printMVPs();
        //printAllNBAs();
        printRecords();
    }

    private static void simSeason() throws FileNotFoundException, InterruptedException {
        Main.isPlayoff(false);
        ArrayList<Game> games = simLeague();
        //showAllGames(games);

//        for(Player p : playersByStat())
//            System.out.println(p + ": " + p.getHighPts());

        System.out.println("-- " + year + " SEASON");
        leaders(games);
        printTable();
        //showGame(games);
        //showPlayer(games);
        /*for(Team t : allTeams)
            for(int p = 0; p < 5; p++) {
                t.getPlayer(p).printSeasonInfo();
            }*/
        playoffs();
        for(Team t : allTeams)
            for(int p = 0; p < 5; p++) {
                t.getPlayer(p).zeroStats();
            }
        offseason();
        draft();
        /*System.out.println("\n\nNEW ROSTERS:\n");
        for(Team t : allTeams) {
            t.printInfo();
        }*/
        zeroTeamStats();

    }

    private static Game simulateRandom() throws FileNotFoundException, InterruptedException {
        Random rnd = new Random();
        int t1 = rnd.nextInt(30) + 1;
        int t2 = rnd.nextInt(30) + 1;
        while(t1 == t2)
            t2 = rnd.nextInt(30) + 1;

        return Main.fullSim(allTeams, 1, t1, t2);
    }

    private static ArrayList<Game> simLeague() throws FileNotFoundException, InterruptedException {
        ArrayList<Game> games = new ArrayList<>();
        Main.fastMode(true);
        for(int z = 0; z < 82; z++) {
            ArrayList<Integer> teamIndeces = new ArrayList<>();
            for (int i = 0; i < 30; i++)
                teamIndeces.add(i + 1);
            Random rnd = new Random();
            while (teamIndeces.size() > 1) {
                int t1 = rnd.nextInt(teamIndeces.size());
                int t2 = rnd.nextInt(teamIndeces.size());
                while (t1 == t2)
                    t2 = rnd.nextInt(teamIndeces.size());

                Game g = Main.fullSim(allTeams, 1, teamIndeces.get(t1), teamIndeces.get(t2));
                games.add(g);
                teamIndeces.remove(t1);
                if (t2 < t1)
                    teamIndeces.remove(t2);
                else
                    teamIndeces.remove(t2 - 1);
            }
        }

        return games;
    }

    private static void playoffs() throws FileNotFoundException, InterruptedException {
        System.out.println("---------------");
        Team[] west = new Team[8];
        Team[] east = new Team[8];
        ArrayList<ArrayList<Team>> conf = conferences();
        for(int i = 0; i < 8; i++) {
            west[i] = conf.get(0).get(i);
            west[i].setSeed(i + 1);
            east[i] = conf.get(1).get(i);
            east[i].setSeed(i + 1);
        }

        System.out.println("PLAYOFF PICTURE:");
        System.out.println("WEST:");
        for(int i = 1; i <= 4; i++) {
            System.out.printf("%s vs. %s%n", west[i - 1].toStringPlayoffs(), west[8 - i].toStringPlayoffs());
        }
        System.out.println("EAST:");
        for(int i = 1; i <= 4; i++) {
            System.out.printf("%s vs. %s%n", east[i - 1].toStringPlayoffs(), east[8 - i].toStringPlayoffs());
        }

        System.out.println("---------------");

        //first round
        System.out.println("First round:");
        Team[] firstRoundWinners = new Team[8];
        for(int i = 1; i <= 4; i++) {
            firstRoundWinners[i - 1] = playoffSeriesWinner(west[i - 1], west[8 - i]);
        }
        System.out.println();
        for(int i = 1; i <= 4; i++) {
            firstRoundWinners[i + 3] = playoffSeriesWinner(east[i - 1], east[8 - i]);
        }

        //second round
        System.out.println("\nSecond round:");
        Team[] secondRoundWinners = new Team[4];
        secondRoundWinners[0] = playoffSeriesWinner(firstRoundWinners[0], firstRoundWinners[3]);    //1v8
        secondRoundWinners[1] = playoffSeriesWinner(firstRoundWinners[1], firstRoundWinners[2]);    //2v7
        System.out.println();
        secondRoundWinners[2] = playoffSeriesWinner(firstRoundWinners[4], firstRoundWinners[7]);    //3v6
        secondRoundWinners[3] = playoffSeriesWinner(firstRoundWinners[5], firstRoundWinners[6]);    //4v5

        //semifinals
        System.out.println("\nSemi-finals:");
        Team[] semiWinners = new Team[2];
        System.out.println("Western semifinals:");
        semiWinners[0] = playoffSeriesWinner(secondRoundWinners[0], secondRoundWinners[1]);    //1v4
        System.out.println("Eastern semifinals:");
        semiWinners[1] = playoffSeriesWinner(secondRoundWinners[2], secondRoundWinners[3]);    //2v3

        //finals
        System.out.println("\nNBA Finals:");
        Team champ = finalsWinner(semiWinners[0], semiWinners[1]);
        for(int p = 0; p < 5; p++)
            champ.getPlayer(p).addChampionshipsCount();
        System.out.println("\n" + champ + " are the NBA champions!");
        Player finalsMVP = champ.getLeader(0);
        finalsMVP.addFinalsMVPCount();
        System.out.printf("%s (%.1f/%.1f/%.1f) is the finals MVP!", finalsMVP, finalsMVP.calculatePerGame(1), finalsMVP.calculatePerGame(2), finalsMVP.calculatePerGame(3));

    }

    private static Team playoffSeriesWinner(Team t1, Team t2) throws FileNotFoundException, InterruptedException {
        t1.zeroSeries();
        t2.zeroSeries();
        int team1 = allTeams.indexOf(t1) + 1;
        int team2 = allTeams.indexOf(t2) + 1;
        ArrayList<String> recap = new ArrayList<>();
        Main.isPlayoff(true);
        while(t1.getSeriesWins() < 4 && t2.getSeriesWins() < 4) {
            Game g = Main.fullSim(allTeams, 1, team1, team2);
            recap.add(String.format("(%s)", g.shortString()));
        }
        while(recap.size() < 7)
            recap.add("");
        String score = String.format("%s vs. %s (%d-%d)", t1.toStringPlayoffs(), t2.toStringPlayoffs(), t1.getSeriesWins(), t2.getSeriesWins());
        String recapTable = String.format("%-12s".repeat(recap.size()), recap.get(0), recap.get(1), recap.get(2), recap.get(3), recap.get(4), recap.get(5), recap.get(6));
        System.out.printf("%s%s%s%n", score, " ".repeat(70 - score.length()), recapTable);

        if(t1.getSeriesWins() == 4)
            return t1;
        else
            return t2;
    }

    private static Team finalsWinner(Team t1, Team t2) throws FileNotFoundException, InterruptedException {
        t1.zeroSeries();
        t2.zeroSeries();

        //set all players' stats to zero for the finals
        for(int i = 0; i < 5; i++)
            t1.getPlayer(i).zeroStats();
        for(int i = 0; i < 5; i++)
            t2.getPlayer(i).zeroStats();

        int team1 = allTeams.indexOf(t1) + 1;
        int team2 = allTeams.indexOf(t2) + 1;
        Main.isPlayoff(true);
        int gameCounter = 1;
        while(t1.getSeriesWins() < 4 && t2.getSeriesWins() < 4) {
            Game g = Main.fullSim(allTeams, 1, team1, team2);
            System.out.printf("Game %d: %s%n", gameCounter, g);
            gameCounter++;
        }
        System.out.printf("%n%s vs. %s (%d-%d)%n", t1.toStringPlayoffs(), t2.toStringPlayoffs(), t1.getSeriesWins(), t2.getSeriesWins());

        if(t1.getSeriesWins() == 4)
            return t1;
        else
            return t2;
    }

    private static void showAllGames(ArrayList<Game> games) {
        System.out.println("\n---------------");
        for(int i = 1; i <= games.size(); i ++)
            System.out.println("Game " + i + ": " + games.get(i - 1));
    }

    private static void leaders(ArrayList<Game> games) {

        int statSize = 8;
        Player[] ovrLeaders = new Player[statSize];

        for(Game g : games) {

            Player[] leaders = new Player[statSize];

            for(int i = 0; i < statSize; i++) {

                if(i == 0) {
                    if (g.getTeam(1).getLeader(i).calculateSeasonEval() > g.getTeam(2).getLeader(i).calculateSeasonEval()) {
                        leaders[0] = g.getTeam(1).getLeader(i);
                    } else if (g.getTeam(1).getLeader(i).calculateSeasonEval() < g.getTeam(2).getLeader(i).calculateSeasonEval()) {
                        leaders[0] = g.getTeam(2).getLeader(i);
                    } else {
                        leaders[0] = g.getWinner().getLeader(i);
                    }

                    if(ovrLeaders[i] == null || ovrLeaders[i].calculateSeasonEval() < leaders[i].calculateSeasonEval())
                        ovrLeaders[i] = leaders[i];
                }

                else if(i == 1) {
                    if (g.getTeam(1).getLeader(i).getSeasonPts() > g.getTeam(2).getLeader(i).getSeasonPts()) {
                        leaders[1] = g.getTeam(1).getLeader(i);
                    } else if (g.getTeam(1).getLeader(i).getSeasonPts() < g.getTeam(2).getLeader(i).getSeasonPts()) {
                        leaders[1] = g.getTeam(2).getLeader(i);
                    } else {
                        leaders[1] = g.getWinner().getLeader(i);
                    }

                    if(ovrLeaders[i] == null || ovrLeaders[i].getSeasonPts() < leaders[i].getSeasonPts())
                        ovrLeaders[i] = leaders[i];
                }

                else if(i == 2) {
                    if (g.getTeam(1).getLeader(i).getSeasonRebs() > g.getTeam(2).getLeader(i).getSeasonRebs()) {
                        leaders[2] = g.getTeam(1).getLeader(i);
                    } else if (g.getTeam(1).getLeader(i).getSeasonRebs() < g.getTeam(2).getLeader(i).getSeasonRebs()) {
                        leaders[2] = g.getTeam(2).getLeader(i);
                    } else {
                        leaders[2] = g.getWinner().getLeader(i);
                    }

                    if(ovrLeaders[i] == null || ovrLeaders[i].getSeasonRebs() < leaders[i].getSeasonRebs())
                        ovrLeaders[i] = leaders[i];
                }

                else if(i == 3) {
                    if (g.getTeam(1).getLeader(i).getSeasonAsts() > g.getTeam(2).getLeader(i).getSeasonAsts()) {
                        leaders[3] = g.getTeam(1).getLeader(i);
                    } else if (g.getTeam(1).getLeader(i).getSeasonAsts() < g.getTeam(2).getLeader(i).getSeasonAsts()) {
                        leaders[3] = g.getTeam(2).getLeader(i);
                    } else {
                        leaders[3] = g.getWinner().getLeader(i);
                    }

                    if(ovrLeaders[i] == null || ovrLeaders[i].getSeasonAsts() < leaders[i].getSeasonAsts())
                        ovrLeaders[i] = leaders[i];
                }

                else if(i == 4) {
                    if (g.getTeam(1).getLeader(i).getSeasonStls() > g.getTeam(2).getLeader(i).getSeasonStls()) {
                        leaders[4] = g.getTeam(1).getLeader(i);
                    } else if (g.getTeam(1).getLeader(i).getSeasonStls() < g.getTeam(2).getLeader(i).getSeasonStls()) {
                        leaders[4] = g.getTeam(2).getLeader(i);
                    } else {
                        leaders[4] = g.getWinner().getLeader(i);
                    }

                    if(ovrLeaders[i] == null || ovrLeaders[i].getSeasonStls() < leaders[i].getSeasonStls())
                        ovrLeaders[i] = leaders[i];
                }

                else if(i == 5) {
                    if (g.getTeam(1).getLeader(i).getSeasonFGpct() > g.getTeam(2).getLeader(i).getSeasonFGpct()) {
                        leaders[5] = g.getTeam(1).getLeader(i);
                    } else if (g.getTeam(1).getLeader(i).getSeasonFGpct() < g.getTeam(2).getLeader(i).getSeasonFGpct()) {
                        leaders[5] = g.getTeam(2).getLeader(i);
                    } else {
                        leaders[5] = g.getWinner().getLeader(i);
                    }

                    if(ovrLeaders[i] == null || ovrLeaders[i].getSeasonFGpct() < leaders[i].getSeasonFGpct() ||
                            (ovrLeaders[i].getSeasonFGpct() == leaders[i].getSeasonFGpct() && ovrLeaders[i].getSeasonFGM() < leaders[i].getSeasonFGM()))
                        ovrLeaders[i] = leaders[i];
                }

                else if(i == 6) {
                    if (g.getTeam(1).getLeader(i).getSeason3Ppct() > g.getTeam(2).getLeader(i).getSeason3Ppct()) {
                        leaders[6] = g.getTeam(1).getLeader(i);
                    } else if (g.getTeam(1).getLeader(i).getSeason3Ppct() < g.getTeam(2).getLeader(i).getSeason3Ppct()) {
                        leaders[6] = g.getTeam(2).getLeader(i);
                    } else {
                        leaders[6] = g.getWinner().getLeader(i);
                    }

                    if(ovrLeaders[i] == null || ovrLeaders[i].getSeason3Ppct() < leaders[i].getSeason3Ppct() ||
                            (ovrLeaders[i].getSeason3Ppct() == leaders[i].getSeason3Ppct() && ovrLeaders[i].getSeason3PM() < leaders[i].getSeason3PM()))
                        ovrLeaders[i] = leaders[i];
                }

                else if(i == 7) {
                    if (g.getTeam(1).getLeader(i).getSeason3PM() > g.getTeam(2).getLeader(i).getSeason3PM()) {
                        leaders[7] = g.getTeam(1).getLeader(i);
                    } else if (g.getTeam(1).getLeader(i).getSeason3PM() < g.getTeam(2).getLeader(i).getSeason3PM()) {
                        leaders[7] = g.getTeam(2).getLeader(i);
                    } else {
                        leaders[7] = g.getWinner().getLeader(i);
                    }

                    if(ovrLeaders[i] == null || ovrLeaders[i].getSeason3PM() < leaders[i].getSeason3PM())
                        ovrLeaders[i] = leaders[i];
                }

            }

        }

        Player mvp = mvp();
        Player roty = null;
        Player[] allRookie = null;
        if(allRookie() != null) {
            roty = allRookie()[0];
            allRookie = allRookie();
        }
        Player[] allNBA = allNBA();
        Player[] records = records();

        System.out.println("\nSEASON LEADERS:");
        System.out.printf(" Highest eval: %s (%s) - %.1f/%.1f/%.1f%n", ovrLeaders[0], ovrLeaders[0].getTeam().shortName(), ovrLeaders[0].calculatePerGame(1), ovrLeaders[0].calculatePerGame(2), ovrLeaders[0].calculatePerGame(3));
        System.out.printf(" Highest scorer: %s (%s) - %.2f%n", ovrLeaders[1], ovrLeaders[1].getTeam().shortName(), ovrLeaders[1].calculatePerGame(1));
        System.out.printf(" Top rebounder: %s (%s) - %.2f%n", ovrLeaders[2], ovrLeaders[2].getTeam().shortName(), ovrLeaders[2].calculatePerGame(2));
        System.out.printf(" Most assists: %s (%s) - %.2f%n", ovrLeaders[3], ovrLeaders[3].getTeam().shortName(), ovrLeaders[3].calculatePerGame(3));
        System.out.printf(" Most steals: %s (%s) - %.2f%n", ovrLeaders[4], ovrLeaders[4].getTeam().shortName(), ovrLeaders[4].calculatePerGame(4));
        System.out.printf(" Highest field goal percentage: %s (%s) - %d%%%n", ovrLeaders[5], ovrLeaders[5].getTeam().shortName(), ovrLeaders[5].getSeasonFGpct());
        System.out.printf(" Highest three point percentage: %s (%s) - %d%%%n", ovrLeaders[6], ovrLeaders[6].getTeam().shortName(), ovrLeaders[6].getSeason3Ppct());
        System.out.printf(" Most threes made: %s (%s) - %d (%.2f)%n", ovrLeaders[7], ovrLeaders[7].getTeam().shortName(), ovrLeaders[7].getSeason3PM(), ovrLeaders[7].calculatePerGame(5));

        System.out.println("\nGAME RECORDS:");
        System.out.printf(" Points: %s (%s) - %d%n", records[0], records[0].getTeam().shortName(), records[0].getHighPts());
        System.out.printf(" Rebounds: %s (%s) - %d%n", records[1], records[1].getTeam().shortName(), records[1].getHighRebs());
        System.out.printf(" Assists: %s (%s) - %d%n", records[2], records[2].getTeam().shortName(), records[2].getHighAsts());
        System.out.printf(" Steals: %s (%s) - %d%n", records[3], records[3].getTeam().shortName(), records[3].getHighStls());
        System.out.printf(" Threes made: %s (%s) - %d%n", records[4], records[4].getTeam().shortName(), records[4].getHigh3PM());

        System.out.println("\nAll-NBA First Team:");
        System.out.printf("PG: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[0], allNBA[0].getTeam().shortName(), allNBA[0].calculatePerGame(1), allNBA[0].calculatePerGame(2), allNBA[0].calculatePerGame(3), allNBA[0].calculateSeasonEval() / 20);
        System.out.printf("SG: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[1], allNBA[1].getTeam().shortName(), allNBA[1].calculatePerGame(1), allNBA[1].calculatePerGame(2), allNBA[1].calculatePerGame(3), allNBA[1].calculateSeasonEval() / 20);
        System.out.printf("SF: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[2], allNBA[2].getTeam().shortName(), allNBA[2].calculatePerGame(1), allNBA[2].calculatePerGame(2), allNBA[2].calculatePerGame(3), allNBA[2].calculateSeasonEval() / 20);
        System.out.printf("PF: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[3], allNBA[3].getTeam().shortName(), allNBA[3].calculatePerGame(1), allNBA[3].calculatePerGame(2), allNBA[3].calculatePerGame(3), allNBA[3].calculateSeasonEval() / 20);
        System.out.printf(" C: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[4], allNBA[4].getTeam().shortName(), allNBA[4].calculatePerGame(1), allNBA[4].calculatePerGame(2), allNBA[4].calculatePerGame(3), allNBA[4].calculateSeasonEval() / 20);

        System.out.println("\nAll-NBA Second Team:");
        System.out.printf("PG: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[5], allNBA[5].getTeam().shortName(), allNBA[5].calculatePerGame(1), allNBA[5].calculatePerGame(2), allNBA[5].calculatePerGame(3), allNBA[5].calculateSeasonEval() / 20);
        System.out.printf("SG: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[6], allNBA[6].getTeam().shortName(), allNBA[6].calculatePerGame(1), allNBA[6].calculatePerGame(2), allNBA[6].calculatePerGame(3), allNBA[6].calculateSeasonEval() / 20);
        System.out.printf("SF: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[7], allNBA[7].getTeam().shortName(), allNBA[7].calculatePerGame(1), allNBA[7].calculatePerGame(2), allNBA[7].calculatePerGame(3), allNBA[7].calculateSeasonEval() / 20);
        System.out.printf("PF: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[8], allNBA[8].getTeam().shortName(), allNBA[8].calculatePerGame(1), allNBA[8].calculatePerGame(2), allNBA[8].calculatePerGame(3), allNBA[8].calculateSeasonEval() / 20);
        System.out.printf(" C: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[9], allNBA[9].getTeam().shortName(), allNBA[9].calculatePerGame(1), allNBA[9].calculatePerGame(2), allNBA[9].calculatePerGame(3), allNBA[9].calculateSeasonEval() / 20);

        System.out.println("\nAll-NBA Third Team:");
        System.out.printf("PG: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[10], allNBA[10].getTeam().shortName(), allNBA[10].calculatePerGame(1), allNBA[10].calculatePerGame(2), allNBA[10].calculatePerGame(3), allNBA[10].calculateSeasonEval() / 20);
        System.out.printf("SG: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[11], allNBA[11].getTeam().shortName(), allNBA[11].calculatePerGame(1), allNBA[11].calculatePerGame(2), allNBA[11].calculatePerGame(3), allNBA[11].calculateSeasonEval() / 20);
        System.out.printf("SF: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[12], allNBA[12].getTeam().shortName(), allNBA[12].calculatePerGame(1), allNBA[12].calculatePerGame(2), allNBA[12].calculatePerGame(3), allNBA[12].calculateSeasonEval() / 20);
        System.out.printf("PF: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[13], allNBA[13].getTeam().shortName(), allNBA[13].calculatePerGame(1), allNBA[13].calculatePerGame(2), allNBA[13].calculatePerGame(3), allNBA[13].calculateSeasonEval() / 20);
        System.out.printf(" C: %s (%s) - %.1f/%.1f/%.1f (%d votes)%n", allNBA[14], allNBA[14].getTeam().shortName(), allNBA[14].calculatePerGame(1), allNBA[14].calculatePerGame(2), allNBA[14].calculatePerGame(3), allNBA[14].calculateSeasonEval() / 20);

        if(allRookie != null) {
            System.out.println("\nAll-Rookie Team:");
            for(int i = 0; i < 5; i++) {
                System.out.printf("%d: %s (%s) (%s) - %.1f/%.1f/%.1f (%d votes)%n", i+1, allRookie[i], allRookie[i].getPositionStr(), allRookie[i].getTeam().shortName(), allRookie[i].calculatePerGame(1), allRookie[i].calculatePerGame(2), allRookie[i].calculatePerGame(3), allRookie[i].calculateSeasonEval() / 20);
            }
        }

        if(roty != null)
            System.out.printf("\nRookie of the year: %s (%s) - %.1f/%.1f/%.1f%n", roty, roty.getTeam().shortName(), roty.calculatePerGame(1), roty.calculatePerGame(2), roty.calculatePerGame(3));
        System.out.printf("\nMVP: %s (%s) - %.1f/%.1f/%.1f%n", mvp, mvp.getTeam().shortName(), mvp.calculatePerGame(1), mvp.calculatePerGame(2), mvp.calculatePerGame(3));
    }

    private static void showGame(ArrayList<Game> games) {
        Scanner in = new Scanner(System.in);
        String gameNum = in.next();
        try {
            while (Integer.parseInt(gameNum) > 0 && Integer.parseInt(gameNum) <= games.size()) {
                games.get(Integer.parseInt(gameNum) - 1).printBox();
                gameNum = in.next();
            }
        } catch (NumberFormatException e) {}
    }

    private static void showPlayer(ArrayList<Game> games) {
        Scanner in = new Scanner(System.in).useDelimiter("\\n");
        boolean cont = true;
        while(cont) {
            String player = in.next();
            boolean found = false;
            for (Player p : playersByStat()) {
                if (player.equals(p.toString())) {
                    p.printSeasonInfo();
                    found = true;
                    break;
                }
            }
            if(!found)
                cont = false;
        }
    }

    private static void printTable() {
        System.out.println("---------------\nSTANDINGS:");
        System.out.printf("%s%s%s%n", "WESTERN CONFERENCE", " ".repeat(32), "EASTERN CONFERENCE");
        ArrayList<ArrayList<Team>> conf = conferences();
        for(int t = 0; t < conf.get(0).size(); t++) {
            String west = String.format("%d. %s: %d-%d", t + 1, conf.get(0).get(t), conf.get(0).get(t).getWins(), conf.get(0).get(t).getLosses());
            String east = String.format("%d. %s: %d-%d", t + 1, conf.get(1).get(t), conf.get(1).get(t).getWins(), conf.get(1).get(t).getLosses());
            System.out.printf("%s%s%s%n", west, " ".repeat(50 - west.length()), east);
        }
    }

    private static Player mvp() {
        int topNumber = 8;
        Team[] top = new Team[topNumber];
        ArrayList<Team> teams = (ArrayList<Team>) allTeams.clone();
        sortByRecord(teams, 0, teams.size() - 1);
        for(int i = 0; i < topNumber; i++)
            top[i] = teams.get(i);

        Player mvp = top[0].getPlayer(0);
        for (Team t : top) {
            for (int p = 0; p < 5; p++) {
                Player check = t.getPlayer(p);
                if (check.calculateSeasonEval() > mvp.calculateSeasonEval())
                    mvp = check;
            }
        }

        return mvp;
    }

    private static Player[] allNBA () {
        Player mvp = mvp();
        Player placeholder = playersByEval()[playersByEval().length - 1];

        //1st team
        Player pg1 = placeholder;
        Player sg1 = placeholder;
        Player sf1 = placeholder;
        Player pf1 = placeholder;
        Player c1 = placeholder;

        switch (mvp.getPosition()) {
            case 0: pg1 = mvp; break;
            case 1: sg1 = mvp; break;
            case 2: sf1 = mvp; break;
            case 3: pf1 = mvp; break;
            case 4: c1 = mvp; break;
        }

        for(Team t : allTeams) {
            if(t.getPlayer(0).calculateSeasonEval() > pg1.calculateSeasonEval() && pg1 != mvp)
                pg1 = t.getPlayer(0);
            else if(t.getPlayer(1).calculateSeasonEval() > sg1.calculateSeasonEval() && sg1 != mvp)
                sg1 = t.getPlayer(1);
            else if(t.getPlayer(2).calculateSeasonEval() > sf1.calculateSeasonEval() && sf1 != mvp)
                sf1 = t.getPlayer(2);
            else if(t.getPlayer(3).calculateSeasonEval() > pf1.calculateSeasonEval() && pf1 != mvp)
                pf1 = t.getPlayer(3);
            else if(t.getPlayer(4).calculateSeasonEval() > c1.calculateSeasonEval() && c1 != mvp)
                c1 = t.getPlayer(4);
        }

        //2nd team
        Player pg2 = placeholder;
        Player sg2 = placeholder;
        Player sf2 = placeholder;
        Player pf2 = placeholder;
        Player c2 = placeholder;

        for(Team t : allTeams) {
            if(t.getPlayer(0).calculateSeasonEval() > pg2.calculateSeasonEval() && t.getPlayer(0) != pg1)
                pg2 = t.getPlayer(0);
            else if(t.getPlayer(1).calculateSeasonEval() > sg2.calculateSeasonEval() && t.getPlayer(1) != sg1)
                sg2 = t.getPlayer(1);
            else if(t.getPlayer(2).calculateSeasonEval() > sf2.calculateSeasonEval() && t.getPlayer(2) != sf1)
                sf2 = t.getPlayer(2);
            else if(t.getPlayer(3).calculateSeasonEval() > pf2.calculateSeasonEval() && t.getPlayer(3) != pf1)
                pf2 = t.getPlayer(3);
            else if(t.getPlayer(4).calculateSeasonEval() > c2.calculateSeasonEval() && t.getPlayer(4) != c1)
                c2 = t.getPlayer(4);
        }

        //3rd team
        Player pg3 = placeholder;
        Player sg3 = placeholder;
        Player sf3 = placeholder;
        Player pf3 = placeholder;
        Player c3 = placeholder;

        for(Team t : allTeams) {
            if(t.getPlayer(0).calculateSeasonEval() > pg3.calculateSeasonEval() && t.getPlayer(0) != pg1 && t.getPlayer(0) != pg2)
                pg3 = t.getPlayer(0);
            else if(t.getPlayer(1).calculateSeasonEval() > sg3.calculateSeasonEval() && t.getPlayer(1) != sg1 && t.getPlayer(1) != sg2)
                sg3 = t.getPlayer(1);
            else if(t.getPlayer(2).calculateSeasonEval() > sf3.calculateSeasonEval() && t.getPlayer(2) != sf1 && t.getPlayer(2) != sf2)
                sf3 = t.getPlayer(2);
            else if(t.getPlayer(3).calculateSeasonEval() > pf3.calculateSeasonEval() && t.getPlayer(3) != pf1 && t.getPlayer(3) != pf2)
                pf3 = t.getPlayer(3);
            else if(t.getPlayer(4).calculateSeasonEval() > c3.calculateSeasonEval() && t.getPlayer(4) != c1 && t.getPlayer(4) != c2)
                c3 = t.getPlayer(4);
        }

        mvp.addMvpCount();
        Player[] allNBA = {pg1, sg1, sf1, pf1, c1, pg2, sg2, sf2, pf2, c2, pg3, sg3, sf3, pf3, c3};
        for(int i = 0; i < 5; i++) {
            allNBA[i].addAllNBA(1);
            allNBA[i+5].addAllNBA(2);
            allNBA[i+10].addAllNBA(3);
            if(!allNBATotal.contains(allNBA[i]))
                allNBATotal.add(allNBA[i]);
            if(!allNBATotal.contains(allNBA[i+5]))
                allNBATotal.add(allNBA[i+5]);
            if(!allNBATotal.contains(allNBA[i+10]))
                allNBATotal.add(allNBA[i+10]);
        }

        if(!mvpTotal.contains(mvp))
            mvpTotal.add(mvp);

        return allNBA;
    }
    
    private static Player[] allRookie() {
        ArrayList<Player> allRookies = new ArrayList<>();
        for(Team t : allTeams)
            for(int p = 0; p < 5; p++)
                if(t.getPlayer(p).getDraftYear() == year)
                    allRookies.add(t.getPlayer(p));
        Player[] rookies = allRookies.toArray(new Player[allRookies.size()]);
        sortPlayerByEval(rookies, 0, rookies.length - 1);

        if(rookies.length >= 5) {
            Player[] allRookie = {rookies[rookies.length - 1], rookies[rookies.length - 2], rookies[rookies.length - 3], rookies[rookies.length - 4], rookies[rookies.length - 5]};
            allRookie[0].addRoty();
            for(Player p : allRookie)
                p.addAllRookie();
            return allRookie;
        }
        return null;
    }

    private static Player[] records() {
        Player[] best = new Player[5];

        Player[] allPlayers = new Player[150];
        for(int t = 0; t < allTeams.size(); t++) {
            for (int p = 0; p < 5; p++) {
                allPlayers[t * 5 + p] = allTeams.get(t).getPlayer(p);
            }
        }

        for(int i = 0; i < 5; i++)
            best[i] = allPlayers[0];

        for(Player p : allPlayers) {
            if(p.getHighPts() > best[0].getHighPts())
                best[0] = p;
            if(p.getHighRebs() > best[1].getHighRebs())
                best[1] = p;
            if(p.getHighAsts() > best[2].getHighAsts())
                best[2] = p;
            if(p.getHighStls() > best[3].getHighStls())
                best[3] = p;
            if(p.getHigh3PM() > best[4].getHigh3PM())
                best[4] = p;
        }

        return best;
    }

    private static ArrayList<Team> teamRanking() {
        ArrayList<Team> teams = (ArrayList<Team>) allTeams.clone();
        sortByRecord(teams, 0, teams.size() - 1);
        return teams;
    }

    //draft

    private static void draft() throws FileNotFoundException {
        DraftClass dc = new DraftClass();
        System.out.println("\n\n\n" + year + " DRAFT");
        ArrayList<Team> draftOrder = teamRanking();
        Collections.reverse(draftOrder);
        for(int i = 0; i < 10; i++) {
            String line1 = String.format("%d: %s", i + 1, draftOrder.get(i));
            String line2 = String.format("%d: %s", i + 11, draftOrder.get(i + 10));
            String line3 = String.format("%d: %s", i + 21, draftOrder.get(i + 20));
            System.out.println(line1 + " ".repeat(30 - line1.length()) + line2 + " ".repeat(30 - line2.length()) + line3);
        }
        System.out.println();

        //dc.printDraftClass();

        System.out.println();

        //team picks player
        for(int i = 0; i < 30; i++) {
            Player selected = draftPick(dc, draftOrder, i);
            System.out.println("#" + (i+1) + " " + draftOrder.get(i) + " select " + selected.prospectInfo());
            dc.removePlayer(selected);
        }
        drafts++;
    }

    private static Player draftPick(DraftClass dc, ArrayList<Team> draftOrder, int pick) {
        Team team = draftOrder.get(pick);
        Player replaced = team.getPlayer(0);
        for(int i = 1; i < 5; i++) {
            if(team.getPlayer(i).calculateOverall() < replaced.calculateOverall())
                replaced = team.getPlayer(i);
        }

        Player playerToDraft = dc.sorted().get(0);
        //best available
        if(playerToDraft.calculateOverall() > team.getPlayer(playerToDraft.getPosition()).calculateOverall()) {
            team.addPlayer(playerToDraft.getPosition(), playerToDraft);
            playerToDraft.setTeam(team);
            //System.out.println("replaced");
            return playerToDraft;
        }

        //fill needs
        int count = 0;
        while(playerToDraft.getPosition() != replaced.getPosition()) {
            if(count < dc.sorted().size()) {
                playerToDraft = dc.sorted().get(count);
                count++;
            }
            else {
                dc.sorted().get(0).setTeam(team);
                return dc.sorted().get(0);
            }
        }
        if(playerToDraft.calculateOverall() > replaced.calculateOverall()) {
            team.addPlayer(replaced.getPosition(), playerToDraft);
            playerToDraft.setTeam(team);
            //System.out.println("replaced");
        }
        return playerToDraft;
    }

    public static int getDraftsNumber() { return drafts; }

    //off-season

    private static void offseason() {
        Random rnd = new Random();
        for(Team t : allTeams)
            for(int i = 0; i < 5; i++) {
                Player p = t.getPlayer(i);
                p.addAge();
                if(p.getAge() > 33) {
                    for(int x = 0; x < 5; x++) {
                        p.changeAttribute(x, -(rnd.nextInt(5)));
                    }
                }
                else if(p.getAge() < 28) {
                    for(int x = 0; x < 5; x++) {
                        p.changeAttribute(x, rnd.nextInt(3));
                    }
                }
            }
        year++;
    }

    private static void zeroTeamStats() {
        for(Team t : allTeams)
            t.zeroAllGames();
    }

    //helpers

    private static void printMVPs() {
        //sort
        for(int i = 0; i < mvpTotal.size(); i++) {
            int maxIndex = i;
            for(int j = i + 1; j < mvpTotal.size(); j++) {
                if(mvpTotal.get(j).getMvpCount() > mvpTotal.get(maxIndex).getMvpCount())
                    maxIndex = j;
            }
            //swap
            Player temp = mvpTotal.get(maxIndex);
            mvpTotal.set(maxIndex, mvpTotal.get(i));
            mvpTotal.set(i, temp);
        }
        //print
        System.out.println("\nMVPS:");
        for(Player p : mvpTotal) {
            System.out.printf("-- %s - %d %n", p, p.getMvpCount());
        }
    }

    private static void printAllNBAs() {
        //sort
        for(int i = 0; i < allNBATotal.size(); i++) {
            int maxIndex = i;
            for(int j = i + 1; j < allNBATotal.size(); j++) {
                int totalValue = allNBATotal.get(j).getAllNBA()[0] * 3 + allNBATotal.get(j).getAllNBA()[1] * 2 + allNBATotal.get(j).getAllNBA()[2];
                int maxValue = allNBATotal.get(maxIndex).getAllNBA()[0] * 3 + allNBATotal.get(maxIndex).getAllNBA()[1] * 2 + allNBATotal.get(maxIndex).getAllNBA()[2];
                if(totalValue > maxValue)
                    maxIndex = j;
            }
            //swap
            Player temp = allNBATotal.get(maxIndex);
            allNBATotal.set(maxIndex, allNBATotal.get(i));
            allNBATotal.set(i, temp);
        }
        //print
        System.out.println("\nAll-NBAS:");
        for(Player p : allNBATotal) {
            System.out.printf("-- %s - (%d/%d/%d) %n", p, p.getAllNBA()[0], p.getAllNBA()[1], p.getAllNBA()[2]);
        }
    }

    public static void updateRecords(Record newRecord) {
        if(newRecord.getType() == 0) {
            //points
            scoringHighs.add(newRecord);
            Collections.sort(scoringHighs, Collections.reverseOrder());
            while(scoringHighs.size() > 10)
                scoringHighs.remove(10);
        }
        if(newRecord.getType() == 1) {
            //rebounds
            reboundingHighs.add(newRecord);
            Collections.sort(reboundingHighs, Collections.reverseOrder());
            while(reboundingHighs.size() > 10)
                reboundingHighs.remove(10);
        }
        if(newRecord.getType() == 2) {
            //assists
            assistHighs.add(newRecord);
            Collections.sort(assistHighs, Collections.reverseOrder());
            while(assistHighs.size() > 10)
                assistHighs.remove(10);
        }
        if(newRecord.getType() == 3) {
            //steals
            stealHighs.add(newRecord);
            Collections.sort(stealHighs, Collections.reverseOrder());
            while(stealHighs.size() > 10)
                stealHighs.remove(10);
        }
        if(newRecord.getType() == 4) {
            //threes
            threesHighs.add(newRecord);
            Collections.sort(threesHighs, Collections.reverseOrder());
            while(threesHighs.size() > 10)
                threesHighs.remove(10);
        }
    }

    private static void printRecords() {
        System.out.println("\nScoring records (game): ");
        for(Record r : scoringHighs) {
            System.out.println((scoringHighs.indexOf(r) + 1) + ". " + r);
        }

        System.out.println("\nRebounding records (game): ");
        for(Record r : reboundingHighs) {
            System.out.println((reboundingHighs.indexOf(r) + 1) + ". " + r);
        }

        System.out.println("\nAssist records (game): ");
        for(Record r : assistHighs) {
            System.out.println((assistHighs.indexOf(r) + 1) + ". " + r);
        }

        System.out.println("\nSteal records (game): ");
        for(Record r : stealHighs) {
            System.out.println((stealHighs.indexOf(r) + 1) + ". " + r);
        }

        System.out.println("\nThrees made records (game): ");
        for(Record r : threesHighs) {
            System.out.println((threesHighs.indexOf(r) + 1) + ". " + r);
        }
    }

    private static Player[] playersByStat() {

        Player[] players = new Player[150];
        for(int t = 0; t < allTeams.size(); t++) {
            for (int p = 0; p < 5; p++) {
                players[t * 5 + p] = allTeams.get(t).getPlayer(p);
            }
        }
        sortPlayerByStat(players, 0, players.length - 1);

        //reverse array
        for(int i = 0; i < players.length/2; i++){
            Player temp = players[i];
            players[i] = players[players.length -i -1];
            players[players.length - i - 1] = temp;
        }

        return players;
    }

    private static Player[] playersByEval() {

        Player[] players = new Player[150];
        for(int t = 0; t < allTeams.size(); t++) {
            for (int p = 0; p < 5; p++) {
                players[t * 5 + p] = allTeams.get(t).getPlayer(p);
            }
        }
        sortPlayerByEval(players, 0, players.length - 1);

        //reverse array
        for(int i = 0; i < players.length/2; i++){
            Player temp = players[i];
            players[i] = players[players.length -i -1];
            players[players.length - i - 1] = temp;
        }

        return players;
    }

    private static void sortPlayerByStat(Player[] players, int low, int high) {
        //check for empty or null array
        if (players == null || players.length == 0){
            return;
        }

        if (low >= high){
            return;
        }

        //Get the pivot element from the middle of the list
        int middle = low + (high - low) / 2;
        int pivot = players[middle].getSeasonPts();

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j)
        {
            //Check until all values on left side array are lower than pivot
            while (players[i].getSeasonPts() < pivot)
            {
                i++;
            }
            //Check until all values on left side array are greater than pivot
            while (players[j].getSeasonPts() > pivot)
            {
                j--;
            }
            //Now compare values from both side of lists to see if they need swapping
            //After swapping move the iterator on both lists
            if (i <= j)
            {
                swapPlayers (players, i, j);
                i++;
                j--;
            }
        }
        //Do same operation as above recursively to sort two sub arrays
        if (low < j){
            sortPlayerByStat(players, low, j);
        }
        if (high > i){
            sortPlayerByStat(players, i, high);
        }
    }

    private static void sortPlayerByEval(Player[] players, int low, int high) {
        //check for empty or null array
        if (players == null || players.length == 0){
            return;
        }

        if (low >= high){
            return;
        }

        //Get the pivot element from the middle of the list
        int middle = low + (high - low) / 2;
        int pivot = players[middle].calculateSeasonEval();

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j)
        {
            //Check until all values on left side array are lower than pivot
            while (players[i].calculateSeasonEval() < pivot)
            {
                i++;
            }
            //Check until all values on left side array are greater than pivot
            while (players[j].calculateSeasonEval() > pivot)
            {
                j--;
            }
            //Now compare values from both side of lists to see if they need swapping
            //After swapping move the iterator on both lists
            if (i <= j)
            {
                swapPlayers (players, i, j);
                i++;
                j--;
            }
        }
        //Do same operation as above recursively to sort two sub arrays
        if (low < j){
            sortPlayerByEval(players, low, j);
        }
        if (high > i){
            sortPlayerByEval(players, i, high);
        }
    }

    private static void swapPlayers (Player[] players, int x, int y) {
        Player temp = players[x];
        players[x] = players[y];
        players[y] = temp;
    }

    private static ArrayList<ArrayList<Team>> conferences () {
        ArrayList<Team> teams = teamRanking();
        ArrayList<Team> west = new ArrayList<>();
        ArrayList<Team> east = new ArrayList<>();

        for(Team t : teams) {
            if(t.getConference().equals("West"))
                west.add(t);
            else
                east.add(t);
        }

        ArrayList<ArrayList<Team>> both = new ArrayList<>();
        both.add(west);
        both.add(east);
        return both;
    }

    private static void sortByRecord(ArrayList<Team> teams, int low, int high) {
        //check for empty or null array
        if (teams == null || teams.size() == 0){
            return;
        }

        if (low >= high){
            return;
        }

        //Get the pivot element from the middle of the list
        int middle = low + (high - low) / 2;
        int pivot = teams.get(middle).getLosses();

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j)
        {
            //Check until all values on left side array are lower than pivot
            while (teams.get(i).getLosses() < pivot)
            {
                i++;
            }
            //Check until all values on left side array are greater than pivot
            while (teams.get(j).getLosses() > pivot)
            {
                j--;
            }
            //Now compare values from both side of lists to see if they need swapping
            //After swapping move the iterator on both lists
            if (i <= j)
            {
                swap (teams, i, j);
                i++;
                j--;
            }
        }
        //Do same operation as above recursively to sort two sub arrays
        if (low < j){
            sortByRecord(teams, low, j);
        }
        if (high > i){
            sortByRecord(teams, i, high);
        }
    }

    private static void swap (ArrayList<Team> teams, int x, int y) {
        Team temp = teams.get(x);
        teams.set(x, teams.get(y));
        teams.set(y, temp);
    }

}
