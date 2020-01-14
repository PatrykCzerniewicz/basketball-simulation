/*

Created by Patryk Czerniewicz 2019
Started on 10.28.2019
Finished alpha on 11.22.2019

TO DO: - years pro
       - bench
       -

 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    private static char mode = '0';
    private static boolean fastMode = false, isPlayoff = false;
    private static int quarter = 1;
    private static int gameClock = 720;
    private static int shotClock = 24;
    private static int shotWait = 1000;
    private static byte possessionArrow = -1; //next possession goes to: 0 - team1, 1 - team2
    private static boolean enablePlay = true, enableSim = false;

    private static double finishMultiplier = 0.1;
    private static double shotMultiplier = 0.02;

    private static Team team1 = new Team();
    private static Team team2 = new Team();

    private static Random rnd = new Random();

    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        /*
        Data Loading
         */

        ArrayList<Team> allTeams = loadData();

        /*
        Pre-Game
         */

        //select game mode
        modeSelect();

        //Selecting the teams

        //User-controlled team selection
            System.out.println("Select a team to control:");
            int t, team1Index = -1, team2Index = -1;
            for (t = 1; t <= allTeams.size(); t++) {
                System.out.println(t + " -- " + allTeams.get(t - 1));
            }
            try {
                int teamChoice = in.nextInt();
                if (teamChoice > 0 && teamChoice <= allTeams.size()) {
                    team1 = allTeams.get(teamChoice - 1);
                    team1Index = teamChoice - 1;
                    System.out.println("You selected the " + team1);
                } else {
                    System.out.println("Please input a valid number");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please input a valid number.");
            }

            //CPU's team
            System.out.println("Select a team to play against:");
            for (t = 1; t <= allTeams.size(); t++) {
                if (t == team1Index + 1) continue;
                System.out.println(t + " -- " + allTeams.get(t - 1));
            }
            try {
                int teamChoice = in.nextInt();
                if (teamChoice > 0 && teamChoice <= allTeams.size() && teamChoice != team1Index + 1) {
                    team2 = allTeams.get(teamChoice - 1);
                    team2Index = teamChoice - 1;
                    System.out.println("You selected to play against the " + team2);
                } else {
                    System.out.println("Please input a valid number");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please input a valid number.");
            }


        //Pre-game presentation
        preGamePresentation();

        /*
        Game

        48 mins & 24 s shot clock => 15 s per play on average => 4 plays per minute => 192 plays per game (average)

        A play:
            - taking the ball from your basket to the opponent's half-court (2 - 6 s)
            - decisions (if shot clock > 0):
                - shoot
                    - did it go in?
                        - YES: add points, reset shot clock
                        - NO: who gets the rebound, reset shot clock
                - drive (1 - 2 s)
                    - same as shoot
                - dribble to a spot (1 - 4 s)
                    - reposition everybody
                - pass to teammate (1 s)
                    - if teammate shoots right after the pass, add assist
            - prompts: where is the ball-handler, where are the teammates, where are the opponents, shot clock remaining

         */


        printClock();


        //Tip-off
        tipoff();


        //Actual game

        //if NBA rules
        if(mode == '1') {
            while (enablePlay) {
                if (gameClock == 0) {
                    newQuarter();
                } else if (team1.getPos()) {
                    //User turn
                    if (!enableSim)
                        userTurn();
                    else
                        simulatePlay(team1);
                } else if (team2.getPos()) {
                    //CPU turn
                    simulatePlay(team2);
                }
            }
        }

        //if street rules
        else if(mode == '2') {
            while (enablePlay) {
                if (team1.getScore() < 11 && team2.getScore() < 11) {
                    if (team1.getPos()) {
                        //User turn
                        if (!enableSim)
                            userTurn();
                        else
                            simulatePlay(team1);
                    } else if (team2.getPos()) {
                        //CPU turn
                        simulatePlay(team2);
                    }
                }
                else if(team1.getScore() >= 11) {
                    enablePlay = false;
                    System.out.println(team1 + " has won!");
                }
                else if(team2.getScore() >= 11) {
                    enablePlay = false;
                    System.out.println(team2 + " has won!");
                }
            }
        }

        //end game box score

        printBox();
        team1.printTeamStats();
        team2.printTeamStats();

        playerOfTheGame(true);

    }

    public static Game fullSim(ArrayList<Team> allTeams, int gameMode, int t1, int t2) throws InterruptedException {


        /*
        Pre-Game
         */

        //select game mode
        mode = ("" + gameMode).charAt(0);

        //Selecting the teams

        team1 = allTeams.get(t1 - 1);
        team2 = allTeams.get(t2 - 1);

        for(int i = 0; i < 5; i++) {
            team1.getPlayer(i).flushData();
            team2.getPlayer(i).flushData();
        }


        //Pre-game presentation
        if(!fastMode)
            preGamePresentation();

        /*
        Game

        48 mins & 24 s shot clock => 15 s per play on average => 4 plays per minute => 192 plays per game (average)

        A play:
            - taking the ball from your basket to the opponent's half-court (2 - 6 s)
            - decisions (if shot clock > 0):
                - shoot
                    - did it go in?
                        - YES: add points, reset shot clock
                        - NO: who gets the rebound, reset shot clock
                - drive (1 - 2 s)
                    - same as shoot
                - dribble to a spot (1 - 4 s)
                    - reposition everybody
                - pass to teammate (1 s)
                    - if teammate shoots right after the pass, add assist
            - prompts: where is the ball-handler, where are the teammates, where are the opponents, shot clock remaining

         */

        gameClock = 720;
        quarter = 1;


        printClock();

        enableSim = true;
        enablePlay = true;
        shotWait = 0;


        //Tip-off
        tipoff();


        //Actual game

        //if NBA rules
        if(mode == '1') {
            while (enablePlay) {
                if (gameClock == 0) {
                    newQuarter();
                } else if (team1.getPos()) {
                    //User turn
                    simulatePlay(team1);
                } else if (team2.getPos()) {
                    //CPU turn
                    simulatePlay(team2);
                }
            }
        }

        //if street rules
        else if(mode == '2') {
            while (enablePlay) {
                if (team1.getScore() < 21 && team2.getScore() < 21) {
                    if (team1.getPos()) {
                        //User turn
                        simulatePlay(team1);
                    } else if (team2.getPos()) {
                        //CPU turn
                        simulatePlay(team2);
                    }
                }
                else if(team1.getScore() >= 21) {
                    enablePlay = false;
                    System.out.println(team1 + " has won!");
                }
                else if(team2.getScore() >= 21) {
                    enablePlay = false;
                    System.out.println(team2 + " has won!");
                }
            }
        }

        //end game box score

        //printBox();
        //team1.printTeamStats();
        //team2.printTeamStats();

        //update record
        if(team1.getScore() > team2.getScore()) {
            if(!isPlayoff) {
                team1.addWin();
                team2.addLoss();
            }
            else {
                team1.addSeriesWin();
                team2.addSeriesLoss();
            }
        }
        else if(team1.getScore() < team2.getScore()) {
            if(!isPlayoff) {
                team1.addLoss();
                team2.addWin();
            }
            else {
                team1.addSeriesLoss();
                team2.addSeriesWin();
            }
        }

        playerOfTheGame(false);

        for(int i = 0; i < 5; i++) {
            Player p1 = team1.getPlayer(i);
            Player p2 = team2.getPlayer(i);
            p1.updateSeasonStats();
            p1.updateHighs();
            p2.updateSeasonStats();
            p2.updateHighs();
            SimulateGame.updateRecords(new Record(p1, 0, p1.getPts()));
            SimulateGame.updateRecords(new Record(p2, 0, p2.getPts()));
            SimulateGame.updateRecords(new Record(p1, 1, p1.getRebs()));
            SimulateGame.updateRecords(new Record(p2, 1, p2.getRebs()));
            SimulateGame.updateRecords(new Record(p1, 2, p1.getAsts()));
            SimulateGame.updateRecords(new Record(p2, 2, p2.getAsts()));
            SimulateGame.updateRecords(new Record(p1, 3, p1.getStls()));
            SimulateGame.updateRecords(new Record(p2, 3, p2.getStls()));
            SimulateGame.updateRecords(new Record(p1, 4, p1.get3PM()));
            SimulateGame.updateRecords(new Record(p2, 4, p2.get3PM()));
        }

        Game game = new Game(team1, team2);

        String score = String.format("%s %d:%d %s", team1, team1.getScore(), team2.getScore(), team2);
        String potg = String.format("POTG: %s (%d/%d/%d)", playerOfTheGame(false), playerOfTheGame(false).getPts(), playerOfTheGame(false).getRebs(), playerOfTheGame(false).getAsts());

        String fullReturn = String.format("%s%s%s", score, " ".repeat(60 - score.length()), potg);
        game.setString(fullReturn);
        game.setPOTG(playerOfTheGame(false));

        return game;

    }

    private static void userTurn() throws InterruptedException {

        while(team1.getPos() && shotClock > 0 && gameClock > 0) {
            Player p = team1.hasBall();
            System.out.println("What should " + p + " do?");
            String command = in.next();

            /*
            Available commands:
            dribble / d - dribble to a spot
            pass / p - pass to player; prompt the user to enter number for target player's position
            drive / layup / l - attempt a layup
            mid / m - shoot midrange
            three / t / 3 - shoot three point jumpshot
            sim - simulate possession
            simtoend - simulate to the end of the game
            box / boxscore - print boxscore

            Only look at the first
             */

            if(command.toLowerCase().equals("dribble") || command.toLowerCase().equals("d"))
                dribble(p);
            else if(command.toLowerCase().equals("pass") || command.toLowerCase().equals("p")) {
                System.out.println("Who should " + p + " pass to?");

                Player[] availablePlayers = new Player[4];
                int arrayIndex = 0;
                for(int i = 0; i < 5; i++) {
                    if(!team1.getPlayer(i).getBall()) {
                        availablePlayers[arrayIndex] = team1.getPlayer(i);
                        arrayIndex++;
                    }
                }

                System.out.printf("%d: %s || %d: %s || %d: %s || %d: %s %n", availablePlayers[0].getPosition() + 1, availablePlayers[0], availablePlayers[1].getPosition() + 1, availablePlayers[1], availablePlayers[2].getPosition() + 1, availablePlayers[2], availablePlayers[3].getPosition() + 1, availablePlayers[3]);

                try {
                    int passTo = in.nextInt();
                    if(passTo == availablePlayers[0].getPosition() + 1 || passTo == availablePlayers[1].getPosition() + 1 || passTo == availablePlayers[2].getPosition() + 1 || passTo == availablePlayers[3].getPosition() + 1) {
                        //make pass
                        pass(p, team1.getPlayer(passTo - 1));
                    }
                    else if(passTo == p.getPosition() + 1) {
                        System.out.println(p + " was called for a double dribble");
                        team1.setNoPos();
                        team2.setPos();
                        System.out.println(team2.getPlayer(3) + " inbounds the ball to " + team2.getPlayer(0));
                        team2.getPlayer(0).setBall();
                        resetClock();
                    }
                    else {
                        System.out.println(p + " threw the ball out of bounds");
                        team1.setNoPos();
                        team2.setPos();
                        System.out.println(team2.getPlayer(3) + " inbounds the ball to " + team2.getPlayer(0));
                        team2.getPlayer(0).setBall();
                        resetClock();
                    }
                }
                catch (InputMismatchException e) {
                    System.out.println("You lost a second because you did not input the right number");
                    passTime(1, true);
                }
            }
            else if(command.toLowerCase().equals("drive") || command.toLowerCase().equals("layup") || command.toLowerCase().equals("l"))
                drive(p);
            else if(command.toLowerCase().equals("mid") || command.toLowerCase().equals("m"))
                mid(p);
            else if(command.toLowerCase().equals("three") || command.toLowerCase().equals("t") || command.toLowerCase().equals("3"))
                three(p);
            else if(command.toLowerCase().equals("sim"))
                simulatePlay(team1);
            else if(command.toLowerCase().equals("simtoend")) {
                enableSim = true;
                shotWait = 0;
                simulatePlay(team1);
            }
            else if(command.toLowerCase().equals("box") || command.toLowerCase().equals("boxscore")) {
                printBox();
            }
            else {
                //invalid command
                System.out.println(p + " had no idea what this meant and lost the ball");
                //steal by the opponent
                team1.setNoPos();
                team2.setPos();
                //Which player is in possession of the ball
                Player p3 = team2.getPlayer(p.getPosition());
                p3.addStls();
                p3.setBall();
                System.out.println(playByPlay(21, p3));
                //p3.printInfo();
                passTime(1, false);
                resetClock();
            }

        }

        if(shotClock <= 0) {
            System.out.println("Shot clock violation!");
            team1.setNoPos();
            team2.setPos();
            team2.getPlayer(0).setBall();
            resetClock();
            setupPlay(team2);
        }
    }

    private static void simulatePlay(Team t) throws InterruptedException {
        //mostly used for CPU turn, simulate one play, all random as of now

        Team other;
        if(t == team1)
            other = team2;
        else
            other = team1;

        int passCount = 0, playCount = 0;
        int playRandom, playRandomMax;

        while(t.getPos()) {
            //play happens only if the team has the ball
            while(shotClock > 0 && gameClock >= 0) {
                if (gameClock <= 0) {
                    t.hasBall().setNoBall();
                    return;
                }

                playRandomMax = (int) ((t.hasBall().getAttPass() + t.hasBall().getTenShot()) * 1.2);        //1.2 for the dribble possibility (20%)
                playRandom = rnd.nextInt(playRandomMax);

                //can pass or dribble and finish with a shot

                if (playRandom < t.hasBall().getTenShot() && playCount > 2) {       //shoot the ball only if two plays have been performed

                    //shoot based on player's tendencies;
                    int shotProbability = t.hasBall().getTenThree() * 100 / (t.hasBall().getTenDrive() + t.hasBall().getTenThree()); // % of shot / shot + drive
                    int shotType = rnd.nextInt(100);

                    if(shotType < shotProbability) {
                        //takes a shot      //in 2018-19 the median teams scored 10% of all their points from the midrange and 30% from three point range -- that is where the 1/2 split in the random comes from (teams are twice more likely to shoot a three than a mid range shot)
                        if(rnd.nextInt(3) == 0)
                            mid(t.hasBall());
                        else
                            three(t.hasBall());
                    }
                    else {
                        //drives
                        drive(t.hasBall());
                    }

                    //after shot exit method
                    return;
                }

                else if(playRandom < (t.hasBall().getAttPass() + t.hasBall().getTenShot())) {

                    playCount++;
                    //who can the player pass to?
                    Player[] availablePlayers = new Player[4];
                    int arrayIndex = 0;
                    for(int i = 0; i < 5; i++) {
                        if(!t.getPlayer(i).getBall()) {
                            availablePlayers[arrayIndex] = t.getPlayer(i);
                            arrayIndex++;
                        }
                    }

                    //pass to teammate based on their tendencies (pass + shot tendency = overall possessions of a player)
                    try {
                        int totalPassChance = (availablePlayers[0].getAttPass() + availablePlayers[0].getTenShot()) +
                                (availablePlayers[1].getAttPass() + availablePlayers[1].getTenShot()) +
                                (availablePlayers[2].getAttPass() + availablePlayers[2].getTenShot()) +
                                (availablePlayers[3].getAttPass() + availablePlayers[3].getTenShot());
                        int passToRandom = rnd.nextInt(totalPassChance);
                        if (passToRandom < (availablePlayers[0].getAttPass() + availablePlayers[0].getTenShot()))
                            pass(t.hasBall(), availablePlayers[0]);
                        else if (passToRandom < (availablePlayers[0].getAttPass() + availablePlayers[0].getTenShot() + availablePlayers[1].getAttPass() + availablePlayers[1].getTenShot()))
                            pass(t.hasBall(), availablePlayers[1]);
                        else if (passToRandom < (availablePlayers[0].getAttPass() + availablePlayers[0].getTenShot() + availablePlayers[1].getAttPass() + availablePlayers[1].getTenShot() + availablePlayers[2].getAttPass() + availablePlayers[2].getTenShot()))
                            pass(t.hasBall(), availablePlayers[2]);
                        else
                            pass(t.hasBall(), availablePlayers[3]);
                    }
                    catch(NullPointerException e) {
                        enableSim = false;
                        for (int i = 0; i < 5; i++)
                            if(team1.getPlayer(i).getBall())
                                System.out.println(team1.getPlayer(i));
                        for (int i = 0; i < 5; i++)
                            if(team2.getPlayer(i).getBall())
                                System.out.println(team2.getPlayer(i));
                    }

                    passCount++;
                    if(!t.getPos())
                        //if team lost possession (turnover), exit method
                        return;
                }

                else {
                    playCount++;
                    dribble(t.hasBall());
                }

                if(!enableSim)
                    Thread.sleep(333);
            }

            if(shotClock <= 0) {
                if(!fastMode)
                    System.out.println("Shot clock violation!");
                t.setNoPos();
                other.setPos();
                other.getPlayer(0).setBall();
                resetClock();
                setupPlay(other);
            }

        }
        //if lost ball, exit method
        return;

    }

    private static void setupPlay(Team t) {
        if(!t.getPlayer(0).getBall()) {
            for(int i = 1; i < 5; i++)
                if(t.getPlayer(i).getBall()) {
                    pass(t.getPlayer(i), t.getPlayer(0));
                    break;
                }
        }

        if(!fastMode)
            System.out.printf("%s dribbles the ball up the court %n", t.getPlayer(0).toStringWithNumber());
        t.getPlayer(0).setBall();
        int timeOfSetup = rnd.nextInt(5) + 2;
        passTime(timeOfSetup, true);
        if(timeOfSetup > 3)
            t.setLastPassFrom(null);
    }

    private static void dribble(Player p) {
        try {
            if(!fastMode)
                System.out.println(playByPlay(rnd.nextInt(2), p));
            passTime(rnd.nextInt(2) + 2, true);
            getTeam(p).setLastPassFrom(null);
        } catch (NullPointerException e) { return; }
    }

    private static void pass(Player p1, Player p2) {
        Team playerTeam = getTeam(p1);
        Team other;
        if(playerTeam == team1)
            other = team2;
        else
            other = team1;

        if(p1.getBall()) {
            if(p1 != p2) {
                int turnoverChance = rnd.nextInt(1000);      //As of now base turnovers on random probability; later change to player's positioning(?) or defense stat
                p1.setNoBall();
                if (turnoverChance < 975) {
                    //Pass gets through
                    try {
                        if(!fastMode)
                            System.out.println(playByPlay(rnd.nextInt(4), p1, p2));
                        p2.setBall();
                        getTeam(p2).setLastPassFrom(p1);
                        passTime(2, true);
                    }
                    catch(NullPointerException e) {System.out.println();}
                }
                else {
                    //Turnover
                    try {
                        if(!fastMode)
                            System.out.println(playByPlay(4, p1, p2));
                    } catch(NullPointerException e) { System.out.println(); }

                    //Team loses the ball
                    //Which player is in possession of the ball
                    Player p3 = other.getPlayer(p2.getPosition());
                    playerTeam.setNoPos();
                    other.setPos();
                    p3.addStls();
                    p3.setBall();
                    if(p3.getPosition() != 0)           //resolve a bug where two players have the ball at the same time
                        other.getPlayer(0).setNoBall();
                    if(!fastMode)
                        System.out.println(playByPlay(21, p3));
                    passTime(2, false);
                    resetClock();
                }
            }
            else { System.out.printf("You cannot make the pass to the same player%n"); }
        }
        else { System.out.printf("%s cannot make the pass without the ball!%n", p1); }
    }

    private static void drive(Player p) throws InterruptedException {
        Team playerTeam = getTeam(p);
        Team other;
        if(playerTeam == team1)
            other = team2;
        else
            other = team1;

        if(p.getBall()) {
            //Can drive to the basket
            double chance = ((double)p.getAttFinish() / 160) + finishMultiplier - (((double)other.getPlayer(p.getPosition()).getAttDef() - 80) / 100);
            if(!fastMode)
                System.out.println(playByPlay(rnd.nextInt(5) + 2, p));
            passTime(rnd.nextInt(2) + 2, true);
            p.setNoBall();
            playerTeam.setNoPos();
            Thread.sleep(shotWait);
            if(chance > rnd.nextDouble()) {
                //Makes the layup
                if(!fastMode)
                    System.out.printf("%s makes the basket%n", p);
                p.addFG(true);
                //add assist
                if(playerTeam.getLastPassFrom() != null) {
                    playerTeam.getLastPassFrom().addAsts();
                    if(!fastMode)
                        System.out.printf("Assist by %s%n", playerTeam.getLastPassFrom());
                }
                printScore();
                Thread.sleep(shotWait);
                //other team gets the ball
                resetClock();
                other.setPos();
                other.getPlayer(0).setBall();
                setupPlay(other);
            }
            else {
                //Misses the layup
                if(!fastMode)
                    System.out.printf("%s misses from close range%n", p);
                p.addFG(false);
                printScore();
                Thread.sleep(shotWait);
                rebound(playerTeam);
            }

        }
        else {
            if(!fastMode)
                System.out.printf("%s cannot drive without the ball%n", p);
        }
    }

    private static void mid(Player p) throws InterruptedException {
        Team playerTeam = getTeam(p);
        Team other;
        if(playerTeam == team1)
            other = team2;
        else
            other = team1;

        if(p.getBall()) {
            //Can shoot
            double chance = (((double) p.getAttShot() / 225) + shotMultiplier) * 1.2 - (((double) other.getPlayer(p.getPosition()).getAttDef() - 80) / 100);
            //System.out.println(p + "'s %: " + (double) p.getAttShot() / 225 + ", Chance: " + chance);
            if(!fastMode)
                System.out.println(playByPlay(rnd.nextInt(3) + 7, p));
            passTime(rnd.nextInt(2) + 2, true);
            p.setNoBall();
            playerTeam.setNoPos();
            Thread.sleep(shotWait);
            if(chance > rnd.nextDouble()) {
                //Makes the shot
                if(!fastMode)
                    System.out.printf("%s's two point jump shot goes in%n", p);
                p.addFG(true);
                //add assist
                if(playerTeam.getLastPassFrom() != null) {
                    playerTeam.getLastPassFrom().addAsts();
                    if(!fastMode)
                        System.out.printf("Assist by %s%n", playerTeam.getLastPassFrom());
                }
                printScore();
                Thread.sleep(shotWait);
                resetClock();
                //other team gets the ball
                other.setPos();
                other.getPlayer(0).setBall();
                setupPlay(other);
            }
            else {
                //Misses the shot
                if(!fastMode)
                    System.out.printf("%s misses the jump shot%n", p);
                p.addFG(false);
                printScore();
                Thread.sleep(shotWait);
                rebound(playerTeam);
            }
        }
        else {
            if(!fastMode)
                System.out.printf("%s cannot shoot without the ball%n", p);
        }
    }

    private static void three(Player p) throws InterruptedException {
        Team playerTeam = getTeam(p);
        Team other;
        if(playerTeam == team1)
            other = team2;
        else
            other = team1;

        if(p.getBall()) {
            //Can shoot
            double chance = ((double) p.getAttShot() / 225) + shotMultiplier - (((double) other.getPlayer(p.getPosition()).getAttDef() - 80) / 100);
            //System.out.println(p + "'s %: " + (double) p.getAttShot() / 225 + ", Chance: " + chance);
            if(!fastMode)
                System.out.println(playByPlay(rnd.nextInt(5) + 10, p));
            passTime(rnd.nextInt(2) + 3, true);
            p.setNoBall();
            playerTeam.setNoPos();
            Thread.sleep(shotWait);
            if(chance > rnd.nextDouble()) {
                //Makes the shot
                if(!fastMode)
                    System.out.printf("%s hits the three%n", p);
                p.add3PT(true);
                //add assist
                if(playerTeam.getLastPassFrom() != null) {
                    playerTeam.getLastPassFrom().addAsts();
                    if(!fastMode)
                        System.out.printf("Assist by %s%n", playerTeam.getLastPassFrom());
                }
                printScore();
                Thread.sleep(shotWait);
                resetClock();
                //other team gets the ball
                other.setPos();
                other.getPlayer(0).setBall();
                setupPlay(other);
            }
            else {
                //Misses the shot
                if(!fastMode)
                    System.out.printf("%s misses the three point jump shot%n", p);
                p.add3PT(false);
                printScore();
                Thread.sleep(shotWait);
                rebound(playerTeam);
            }
        }
        else {
            if(!fastMode)
                System.out.printf("%s cannot shoot without the ball%n", p);
        }
    }

    private static void rebound(Team t) {
        //Team passed as parameter is the team who was shooting
        int team1Total = team1.getPlayer(0).getAttReb() + team1.getPlayer(1).getAttReb() + team1.getPlayer(2).getAttReb() + team1.getPlayer(3).getAttReb() + team1.getPlayer(4).getAttReb();
        int team2Total = team2.getPlayer(0).getAttReb() + team2.getPlayer(1).getAttReb() + team2.getPlayer(2).getAttReb() + team2.getPlayer(3).getAttReb() + team2.getPlayer(4).getAttReb();
        int chance = rnd.nextInt(team1Total + team2Total + 700);
        boolean isDefensive = false;
        Player p;
        if(t == team1) {
            //if user's team shot
            if(chance < team1Total) {
                //team1 gets the ball (offensive rebound)
                team1.setPos();
                //which player gets the ball
                int whoRnd = rnd.nextInt(team1Total);
                if(whoRnd < team1.getPlayer(0).getAttReb())
                    p = team1.getPlayer(0);
                else if(whoRnd < team1.getPlayer(0).getAttReb() + team1.getPlayer(1).getAttReb())
                    p = team1.getPlayer(1);
                else if(whoRnd < team1.getPlayer(0).getAttReb() + team1.getPlayer(1).getAttReb() + team1.getPlayer(2).getAttReb())
                    p = team1.getPlayer(2);
                else if(whoRnd < team1.getPlayer(0).getAttReb() + team1.getPlayer(1).getAttReb() + team1.getPlayer(2).getAttReb() + team1.getPlayer(3).getAttReb())
                    p = team1.getPlayer(3);
                else
                    p = team1.getPlayer(4);
            }
            else {
                //team2 gets the ball
                team1.setNoPos();
                //which player gets the ball
                int whoRnd = rnd.nextInt(team2Total);
                if(whoRnd < team2.getPlayer(0).getAttReb())
                    p = team2.getPlayer(0);
                else if(whoRnd < team2.getPlayer(0).getAttReb() + team2.getPlayer(1).getAttReb())
                    p = team2.getPlayer(1);
                else if(whoRnd < team2.getPlayer(0).getAttReb() + team2.getPlayer(1).getAttReb() + team2.getPlayer(2).getAttReb())
                    p = team2.getPlayer(2);
                else if(whoRnd < team2.getPlayer(0).getAttReb() + team2.getPlayer(1).getAttReb() + team2.getPlayer(2).getAttReb() + team2.getPlayer(3).getAttReb())
                    p = team2.getPlayer(3);
                else
                    p = team2.getPlayer(4);
                //pass to point guard
                isDefensive = true;
            }
        }
        else {
            //if opponent shot
            if(chance < team2Total) {
                //team2 gets the ball (offensive rebound)
                team1.setNoPos();
                //which player gets the ball
                int whoRnd = rnd.nextInt(team2Total);
                if(whoRnd < team2.getPlayer(0).getAttReb())
                    p = team2.getPlayer(0);
                else if(whoRnd < team2.getPlayer(0).getAttReb() + team2.getPlayer(1).getAttReb())
                    p = team2.getPlayer(1);
                else if(whoRnd < team2.getPlayer(0).getAttReb() + team2.getPlayer(1).getAttReb() + team2.getPlayer(2).getAttReb())
                    p = team2.getPlayer(2);
                else if(whoRnd < team2.getPlayer(0).getAttReb() + team2.getPlayer(1).getAttReb() + team2.getPlayer(2).getAttReb() + team2.getPlayer(3).getAttReb())
                    p = team2.getPlayer(3);
                else
                    p = team2.getPlayer(4);
            }
            else {
                //team1 gets the ball
                team1.setPos();
                //which player gets the ball
                int whoRnd = rnd.nextInt(team1Total);
                if(whoRnd < team1.getPlayer(0).getAttReb())
                    p = team1.getPlayer(0);
                else if(whoRnd < team1.getPlayer(0).getAttReb() + team1.getPlayer(1).getAttReb())
                    p = team1.getPlayer(1);
                else if(whoRnd < team1.getPlayer(0).getAttReb() + team1.getPlayer(1).getAttReb() + team1.getPlayer(2).getAttReb())
                    p = team1.getPlayer(2);
                else if(whoRnd < team1.getPlayer(0).getAttReb() + team1.getPlayer(1).getAttReb() + team1.getPlayer(2).getAttReb() + team1.getPlayer(3).getAttReb())
                    p = team1.getPlayer(3);
                else
                    p = team1.getPlayer(4);
                //pass to point guard
                isDefensive = true;
            }
        }

        p.addRebs();
        p.setBall();
        if(!fastMode)
            System.out.println(p.toStringWithNumber() + " gets the rebound");
        resetClock();

        team1.setLastPassFrom(null);
        team2.setLastPassFrom(null);

        //Pass to point guard if defensive rebound
        if(isDefensive) {
            if (t == team2 && team1.getPos() && p.getPosition() != 0) {
                pass(p, team1.getPlayer(0));
                setupPlay(team1);
            }
            if (t == team1 && team2.getPos() && p.getPosition() != 0) {
                pass(p, team2.getPlayer(0));
                setupPlay(team2);
            }
        }
    }

    private static void tipoff() throws InterruptedException {
        if(!fastMode)
            System.out.println("The ball is tipped in the air!");
        Thread.sleep(shotWait * 3 / 2);
        if(team1.getPlayer(4).getHeight() + team1.getPlayer(4).getAttReb() > team2.getPlayer(4).getHeight() + team2.getPlayer(4).getAttReb()) {
            //Team1 gets the ball
            team1.setPos();
            team1.getPlayer(0).setBall();
            possessionArrow = 0;
            if(!fastMode)
                System.out.println(team1.getPlayer(4) + " has won the tip! The " + team1 + " are in possession.");
            passTime(2, true);
            if(!fastMode)
                System.out.println(playByPlay(17, team1.getPlayer(0)));
        }
        else if(team1.getPlayer(4).getHeight() + team1.getPlayer(4).getAttReb() < team2.getPlayer(4).getHeight() + team2.getPlayer(4).getAttReb()) {
            //Team2 gets the ball
            team2.setPos();
            team2.getPlayer(0).setBall();
            possessionArrow = 1;
            if(!fastMode)
                System.out.println(team2.getPlayer(4) + " has won the tip! The " + team2 + " are in possession.");
            passTime(2, true);
            if(!fastMode)
                System.out.println(playByPlay(17, team2.getPlayer(0)));
        }
        else {
            int chance = rnd.nextInt(2);
            if(chance == 0) {
                //Team1
                team1.setPos();
                team1.getPlayer(0).setBall();
                possessionArrow = 0;
                if(!fastMode)
                    System.out.println(team1.getPlayer(4) + " has won the tip! The " + team1 + " are in possession.");
                passTime(2, true);
                if(!fastMode)
                    System.out.println(playByPlay(17, team1.getPlayer(0)));
            }
            else {
                //Team2
                team2.setPos();
                team2.getPlayer(0).setBall();
                possessionArrow = 1;
                if(!fastMode)
                    System.out.println(team2.getPlayer(4) + " has won the tip! The " + team2 + " are in possession.");
                passTime(2, true);
                if(!fastMode)
                    System.out.println(playByPlay(17, team2.getPlayer(0)));
            }
        }

        resetClock();
    }

    private static String playByPlay(int index, Player player) {
        String p = player.toStringWithNumber();
        String[] plays = {
                // 0 - 1 Dribbling
                String.format("%s dribbles the ball to the left 45", p),
                String.format("%s dribbles the ball to the right 45", p),
                // 2 - 3 Driving
                String.format("A strong drive by %s", p),
                String.format("%s drives to the basket", p),
                // 4 Layups
                String.format("%s goes for a layup", p),
                // 5 - 6 Dunks
                String.format("%s soars for a dunk!", p),
                String.format("%s attempts to throw it down!", p),
                // 7 - 9 Mid-range
                String.format("%s shoots from the left baseline", p),
                String.format("%s shoots from the free throw line", p),
                String.format("%s shoots from the right baseline", p),
                // 10 - 14 Threes
                String.format("%s shoots the three from the left corner", p),
                String.format("%s shoots the three from the left wing", p),
                String.format("%s attempts the straight-ahead three", p),
                String.format("%s shoots the three from the right wing", p),
                String.format("%s shoots the three from the right corner", p),
                // 15 - 16 Turnover
                String.format("%s turns it over", p),
                String.format("%s loses the ball", p),
                // 17 - 20 Position
                String.format("The ball is in %s's hands", p),
                String.format("%s has the ball on top of the key", p),
                String.format("%s is in the post", p),
                String.format("%s has the ball on the wing", p),
                // 21 Steals
                String.format("The ball is stolen by %s", p)
        };
        return plays[index];
    }

    private static String playByPlay(int index, Player player1, Player player2) {
        String p1 = player1.toStringWithNumber();
        String p2 = player2.toStringWithNumber();
        String[] plays = {
                // 0 - 3 Passing
                String.format("%s passes to %s", p1, p2),
                String.format("%s does a bounce pass to %s", p1, p2),
                String.format("%s dishes it to %s", p1, p2),
                String.format("%s gives a lob pass to %s", p1, p2),
                // 4 Bad pass
                String.format("%s tried to pass it to %s but turned it over", p1, p2),
                // Impact of opponent
                // 5 - 6 On ball defense
                String.format("%s is tightly guarded by %s", p1, p2),
                String.format("%s is given a lot of space by %s", p1, p2),
                // 7 - 9 Off ball defense
                String.format("%s is being contained by %s", p1, p2),
                String.format("%s is all over %s", p1, p2),
                String.format("%s is not being guarded by %s at all", p1, p2),
                // 10 - 11 Shooting
                String.format("%s shoots it over %s", p1, p2),
                String.format("%s tries to shoot but %s is there with a heavy contest", p1, p2)
        };
        return plays[index];
    }

    public static Team getTeam(Player p) {
        if(team1.contains(p))
            return team1;
        else if(team2.contains(p))
            return team2;
        else
            return null;
    }

    private static void printClock() {
        if(!fastMode) {
            if (mode == '1')
                if (quarter <= 4)
                    System.out.printf("%-70s Shot clock: %02d | Game clock: %02d:%02d | Q:%d%n", "", shotClock, gameClock / 60, gameClock % 60, quarter);
                else
                    System.out.printf("%-70s Shot clock: %02d | Game clock: %02d:%02d | OT%d%n", "", shotClock, gameClock / 60, gameClock % 60, quarter - 4);
        }
    }

    private static void resetClock() {
        //Line to separate particular plays

        if(!fastMode)
            System.out.println("-----------------");
        shotClock = 24;
        printClock();
    }

    private static void passTime(int s, boolean show) {
        if(mode == '1') {
            //s += 1;
            shotClock -= s;
            gameClock -= s;
        }

        //ensure the clock does not go into negative numbers
        if(gameClock < 0)
            gameClock = 0;

        if(show && !fastMode)
            printClock();

    }

    private static void newQuarter() {

        int initialGameClock = 720;
        int overtimeClock = 300;

        if(quarter >= 4 && gameClock == 0) {
            if(team1.getScore() == team2.getScore()) {
                //overtime
                quarter++;
                gameClock = overtimeClock;
                shotClock = 24;
                if(team1.getPos())
                    team1.setNoPos();
                else if(team2.getPos())
                    team2.setNoPos();
            }
            else {
                //stop plays
                enablePlay = false;
                team1.setNoPos();
                team2.setNoPos();
                //end game
                if(!fastMode) {
                    System.out.print("END OF GAME\nFINAL SCORE: ");
                    System.out.printf("%s -- %d : %d -- %s%n%n", team1, team1.getScore(), team2.getScore(), team2);
                }
                return;
            }
        }

        else if(quarter < 4) {
            quarter++;
            gameClock = initialGameClock;
            shotClock = 24;
            if(team1.getPos())
                team1.setNoPos();
            else if(team2.getPos())
                team2.setNoPos();
        }

        if(!fastMode) {
            if (quarter == 2) {
                System.out.println("--Start of the " + quarter + "nd quarter");
            } else if (quarter == 3) {
                System.out.println("--Start of the " + quarter + "rd quarter");
            } else if (quarter == 4) {
                System.out.println("--Start of the " + quarter + "th quarter");
            } else {
                System.out.println("--Start of the overtime #" + (quarter - 4));
            }

            printClock();
            printScore();
        }

        if(possessionArrow == 0) {
            team1.setNoPos();
            team2.setPos();
            team2.getPlayer(0).setBall();
            possessionArrow = 1;
            setupPlay(team2);
        }
        else {
            team2.setNoPos();
            team1.setPos();
            team1.getPlayer(0).setBall();
            possessionArrow = 0;
            setupPlay(team1);
        }


    }

    private static void printScore() {
        if(!fastMode)
            System.out.printf("%-120s %s -- %d : %d -- %s%n", "", team1, team1.getScore(), team2.getScore(), team2);
    }

    private static void printBox() {
        System.out.println(team1);
        for(int i = 0; i < 5; i++)
            team1.getPlayer(i).printInfo();
        System.out.println("------------------");
        System.out.println(team2);
        for(int i = 0; i < 5; i++)
            team2.getPlayer(i).printInfo();
    }

    private static Player playerOfTheGame(boolean showReturn) {
        Team t;
        if(team1.getScore() > team2.getScore())
            t = team1;
        else
            t = team2;

        Player maxEval = t.getPlayer(0);
        for(int i = 1; i < 5; i++) {
            int playerEval = t.getPlayer(i).calculateEval();
            if(playerEval > maxEval.calculateEval())
                maxEval = t.getPlayer(i);
        }

        if(showReturn) {
            System.out.println("\n------------------");
            System.out.println("Patryk's Player of the Game: " + maxEval);
        }
        return maxEval;
    }

    public static ArrayList<Team> loadData() throws FileNotFoundException {
        //Create a map with ID as key and a player object as value
        TreeMap<Integer, Player> IDs = new TreeMap<>();
        File players = new File("players.txt");
        Scanner playersIn = new Scanner(players);
        while(playersIn.hasNextLine()) {
            String line = playersIn.nextLine();
            String[] spl = line.split(",");
            IDs.put(Integer.parseInt(spl[0]), new Player(Integer.parseInt(spl[0]), spl[1], spl[2], spl[3], Integer.parseInt(spl[4]), spl[5], Integer.parseInt(spl[6]), Integer.parseInt(spl[7]), Integer.parseInt(spl[8]), Integer.parseInt(spl[9]), Integer.parseInt(spl[10]), Integer.parseInt(spl[11]), Integer.parseInt(spl[12]), Integer.parseInt(spl[13]), Integer.parseInt(spl[14])));
        }
        playersIn.close();

        //Create teams and assign players by ID from text file
        ArrayList<Team> allTeams = new ArrayList<>();

        File teams = new File("teams.txt");
        Scanner teamsIn = new Scanner(teams);
        int lineCount = 0;
        while(teamsIn.hasNextLine()) {
            String line = teamsIn.nextLine();
            String[] spl = line.split(",");
            allTeams.add(new Team(spl[0], spl[1], spl[2], spl[3]));
            for(int i = 4; i < spl.length; i++) {
                allTeams.get(lineCount).addPlayer(i - 4, IDs.get(Integer.parseInt(spl[i])));
                IDs.get(Integer.parseInt(spl[i])).setTeam(allTeams.get(lineCount));
            }
            lineCount++;
        }

        teamsIn.close();

        Collections.sort(allTeams);

        return allTeams;
    }

    private static void modeSelect() {
        //select mode: NBA - normal, STREET - first team to 21 points wins
        System.out.println("Select game mode:");
        System.out.println("1 - NBA Rules (4 quarters, 12 minutes each)");
        System.out.println("2 - Streetball Rules (first team to score 11 points wins)");
        while(mode != '1' && mode != '2') {
            mode = in.next().charAt(0);
            if (mode != '1' && mode != '2')
                System.out.println("Please input a valid number.");
        }
    }

    private static void preGamePresentation() {
        try {
            System.out.println("Welcome to the game between the " + team1 + " and the " + team2 + "!\nHere are the lineups:\n");
            team1.printLineup();
            System.out.println("And the other team...");
            team2.printLineup();
            System.out.println("We are ready for tip-off!");
        }
        catch(NullPointerException | InterruptedException e) {
            System.out.println("There was an error during team selection. Restart program.");
            System.exit(0);
        }
    }

    public static void fastMode(boolean yes) {
        fastMode = yes;
    }

    public static void isPlayoff(boolean yes) {
        isPlayoff = yes;
    }


}
