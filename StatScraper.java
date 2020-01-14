package main.java;

import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.time.Instant;
import java.time.Duration;

public class StatScraper {

    public static void main(String[] args) throws FileNotFoundException {

        boolean fastMode = true;

        double finishMultiplier = 160.0;
        double shotMultiplier = 225.0;
        double rebMultiplier = 5.2;
        double passMultiplier = 7.3;
        double tenFinishMultiplier = 4.2;
        double tenShotMultiplier = 5.8;
        double tenOvrMultiplier = 3.1;

        Instant start = Instant.now();

        PrintWriter out = new PrintWriter("players_new.txt");

        File players = new File("players_test.txt");
        Scanner playersIn = new Scanner(players);
        ArrayList<String[]> allPlayers = new ArrayList<>();
        while(playersIn.hasNextLine()) {
            String line = playersIn.nextLine();
            String[] spl = line.split(",");
            int desiredSize = 15;
            if(spl.length < desiredSize) {
                int previousSize = spl.length;
                spl = Arrays.copyOf(spl, desiredSize);
                for(int i = previousSize; i < desiredSize; i++)
                    spl[i] = "-";
            }
            allPlayers.add(spl);
        }

        String classification = "";
        int playersEligible = 0;

        for(String[] player : allPlayers) {
            String playerName = player[1] + " " + player[2];
            System.out.println(playerName);
            String playerCode = "";
            if(player[2].length() > 4)
                playerCode = player[2].substring(0,5).toLowerCase() + player[1].substring(0,2).toLowerCase();
            else
                playerCode = player[2].toLowerCase() + player[1].substring(0,2).toLowerCase();
            //Names with non-alphabetical characters
            if(player[1].substring(1,2).equals(".") || player[1].substring(1,2).equals("'"))
                playerCode = player[2].substring(0,5).toLowerCase() + player[1].substring(0,1).toLowerCase() + player[1].substring(2,3).toLowerCase();
            String number = "01";

            //Special Cases:

            //Players with number "02"
            if(player[1].equals("Wesley") && player[2].equals("Matthews") ||
               player[1].equals("Tobias") && player[2].equals("Harris") ||
               player[1].equals("Danny") && player[2].equals("Green") ||
               player[1].equals("Kemba") && player[2].equals("Walker") ||
               player[1].equals("Anthony") && player[2].equals("Davis") ||
               player[1].equals("Lou") && player[2].equals("Williams") ||
               player[1].equals("Jaylen") && player[2].equals("Brown") ||
               player[1].equals("Tim") && player[2].equals("Hardaway Jr.") ||
               player[1].equals("Jaren") && player[2].equals("Jackson Jr.")||
               player[1].equals("Bojan") && player[2].equals("Bogdanović")||
               player[1].equals("Harrison") && player[2].equals("Barnes")||
               player[1].equals("Isaiah") && player[2].equals("Thomas") ||
               player[1].equals("Miles") && player[2].equals("Bridges"))
            {
                number = "02";
            }

            //Clint Capela
            if(player[1].equals("Clint") && player[2].equals("Capela")) {
                playerCode = "capelca";
            }

            //Luka Doncic
            if(player[1].equals("Luka") && player[2].equals("Dončić")) {
                playerCode = "doncilu";
            }

            //Dario Saric
            if(player[1].equals("Dario") && player[2].equals("Šarić")) {
                playerCode = "saricda";
            }

            //Nikola Jokic
            if(player[1].equals("Nikola") && player[2].equals("Jokić")) {
                playerCode = "jokicni";
            }

            //Cedi Osman
            if(player[1].equals("Cedi") && player[2].equals("Osman")) {
                playerCode = "osmande";
            }

            //Marcus Morris
            if(player[1].equals("Marcus") && player[2].equals("Morris")) {
                number = "03";
            }

            String address = "https://www.basketball-reference.com/players/" + playerCode.substring(0,1) + "/" + playerCode + number + ".html";
            //System.out.println(address);

            Document doc;

            try {
                doc = Jsoup.connect(address).get();

                //Get Height

                String heightPath = "span[itemprop=\"height\"]";
                Elements Height = doc.select(heightPath);
                String[] heightSplit = Height.text().split("-");
                int heightInInches = Integer.parseInt(heightSplit[0]) * 12 + Integer.parseInt(heightSplit[1]);
                //change value of height
                player[4] = String.valueOf(heightInInches);



                //per game stats

                Element PerGame;
                double mins, twoPM, twoPA, twoPct, threePM, threePA, threePct, rebs, asts;

                //only if played at least 10 games this season
                if(doc.getElementById("per_game.2020") != null && Double.parseDouble(doc.getElementById("per_game.2020").text().split(" ")[5]) > 14) {
                    PerGame = doc.getElementById("per_game.2020");
                    classification = "normal";
                    playersEligible++;
                }
                //if played less than 15 games this season
                else if(doc.getElementById("per_game.2019") != null) {
                    PerGame = doc.getElementById("per_game.2019");
                    classification = "lastseason";
                }
                //if was injured in 2019 and has not played in 2020
                else if(doc.getElementById("per_game.2018") != null) {
                    PerGame = doc.getElementById("per_game.2018");
                    classification = "inj";
                }
                else {
                    PerGame = null;
                    classification = "notplayed";
                    continue;
                }


                String[] array = PerGame.text().split(" ");

                mins = Double.parseDouble(array[7]);
                threePM = Double.parseDouble(array[11]);
                threePA = Double.parseDouble(array[12]);
                threePct = Double.parseDouble(array[13]);
                twoPM = Double.parseDouble(array[14]);
                twoPA = Double.parseDouble(array[15]);
                twoPct = Double.parseDouble(array[16]);
                rebs = Double.parseDouble(array[23]);
                asts = Double.parseDouble(array[24]);

                //Special cases

                //if player has not attempted a three, his percentage on b-r is blank which results in a different data array
                if(threePA == 0.0 && !array[13].equals(".000") && !array[13].equals("1.000")) {
                    threePct = 0.0;
                    twoPM = Double.parseDouble(array[13]);
                    twoPA = Double.parseDouble(array[14]);
                    twoPct = Double.parseDouble(array[15]);
                    rebs = Double.parseDouble(array[22]);
                    asts = Double.parseDouble(array[23]);
                }


                if(!fastMode) {
                    System.out.println("Minutes per game: " + mins);

                    System.out.println("Two pointers made per game: " + twoPM);
                    System.out.println("Two pointers attempted per game: " + twoPA);
                    System.out.println("Two point %: " + twoPct);
                    System.out.println("Finishing: " + createRatingShot(twoPM, twoPA, twoPct, finishMultiplier) + "\n");

                    System.out.println("Three pointers made per game: " + threePM);
                    System.out.println("Three pointers attempted per game: " + threePA);
                    System.out.println("Three point %: " + threePct);
                    System.out.println("Shooting: " + createRatingShot(threePM, threePA, threePct, shotMultiplier) + "\n");

                    System.out.println("Rebounds per game: " + rebs);
                    System.out.println("Rebounds per 48 mins: " + rebs * 48 / mins);
                    System.out.println("Rebound rating: " + createRatingReb(mins, rebs, rebMultiplier) + "\n");

                    System.out.println("Assists per game: " + asts);
                    System.out.println("Assists per 48 mins: " + asts * 48 / mins);
                    System.out.println("Pass rating: " + createRatingPass(mins, asts, passMultiplier) + "\n");

                    System.out.println("2 point attempts per game: " + twoPA);
                    System.out.println("2 point attempts per 48 mins: " + twoPA * 48 / mins);
                    System.out.println("2 point tendency: " + createTendencyShot(mins, twoPA, tenFinishMultiplier) + "\n");

                    System.out.println("3 point attempts per game: " + threePA);
                    System.out.println("3 point attempts per 48 mins: " + threePA * 48 / mins);
                    System.out.println("3 point tendency: " + createTendencyShot(mins, threePA, tenShotMultiplier) + "\n");

                    System.out.println("All attempts per game: " + (twoPA + threePA));
                    System.out.println("All attempts per 48 mins: " + (twoPA + threePA) * 48 / mins);
                    System.out.println("Shot tendency: " + createTendencyShot(mins, (twoPA + threePA), tenOvrMultiplier) + "\n");
                }

                player[6] = "" + createRatingFinish(twoPM, twoPA, twoPct, finishMultiplier);
                player[7] = "" + createRatingShot(threePM, threePA, threePct, shotMultiplier);
                player[9] = "" + createRatingReb(mins, rebs, rebMultiplier);
                player[10] = "" + createRatingPass(mins, asts, passMultiplier);
                player[11] = "" + createTendencyShot(mins, twoPA, tenFinishMultiplier);
                player[12] = "" + createTendencyShot(mins, threePA, tenShotMultiplier);
                player[13] = "" + createTendencyShot(mins, (twoPA + threePA), tenOvrMultiplier);

                //get age
                String agePath = "[data-birth]";
                Elements YearOfBirth = doc.select(agePath);
                String[] yearSplit = YearOfBirth.text().split(", ");
                int age = 2020 - Integer.parseInt(yearSplit[1]);
                player[14] = String.valueOf(age);



            } catch (IOException e) {
                e.printStackTrace();
            }

            //Special cases (manually calculated):

            //Zion Williamson (hasn't played a pro game yet - stats manually calculated from preseason games and adjusted for realistic simulation)
            if(player[1].equals("Zion") && player[2].equals("Williamson")) {
                player[6] = "99";
                player[7] = "56";
                player[9] = "88";
                player[10] = "34";
                player[11] = "95";
                player[12] = "30";
                player[13] = "85";
            }

            //DeMar DeRozan (great midrange scorer but terrible three point shooter), manually adjust shot rating
            if(player[1].equals("DeMar") && player[2].equals("DeRozan")) {
                player[7] = "70";
                player[12] = "25";
            }

            //Luka Doncic (takes tough threes which lowers his percentage but is a much better shooter than the ratings suggest)
            if(player[1].equals("Luka") && player[2].equals("Dončić")) {
                if(Integer.parseInt(player[7]) < 88)
                    player[7] = "88";
            }

            //Joe Harris (led the league in percentage last season and won the all star contest)
            if(player[1].equals("Joe") && player[2].equals("Harris")) {
                if(Integer.parseInt(player[7]) < 98)
                    player[7] = "98";
            }

            //Klay Thompson
            if(player[1].equals("Klay") && player[2].equals("Thompson")) {
                if(Integer.parseInt(player[7]) < 97)
                    player[7] = "97";
            }

            //Marc Gasol (terrible start of the season in terms of 2p%, adjusted to last season's 48%)
            if(player[1].equals("Marc") && player[2].equals("Gasol")) {
                if(Integer.parseInt(player[6]) < 78)
                    player[6] = "78";
            }

            //Andre Drummond (rebounding god)
            if(player[1].equals("Andre") && player[2].equals("Drummond")) {
                    player[9] = "99";
            }

            //Clint Capela (another rebounding god)
            if(player[1].equals("Clint") && player[2].equals("Capela")) {
                    player[9] = "97";
            }

            //Dejounte Murray (too many rebounds)
            if(player[1].equals("Dejounte") && player[2].equals("Murray")) {
                player[9] = "62";
            }

            //Nikola Jokic (passing)
            if(player[1].equals("Nikola") && player[2].equals("Jokić")) {
                player[10] = "83";
            }

        }

        playersIn.close();


        //write to new file
        for (String[] player : allPlayers) {
            String data = "";
            for (String s : player)
                data += s + ",";
            data = data.substring(0, data.length() - 1);
            System.out.println(data);
            out.println(data);
        }
        out.close();

        System.out.printf("Players eligible for 2020: %d/150 (%.0f%%) %n", playersEligible, percentage(playersEligible, 150.0));

        Instant finish = Instant.now();
        long timeOfOp = Duration.between(start, finish).toMillis();
        System.out.println("\nTime of operation: " + timeOfOp / 1000 + "s " + timeOfOp % 1000 + "ms.");
        System.out.println("Average time per player: " + timeOfOp / allPlayers.size() + "ms.");


    }

    private static double percentage(double top, double bottom) {
        if(top == 0.0 || bottom == 0.0)
            return 0.0;
        else {
            double result = top / bottom;
            return Math.floor(result * 1000) / 10;
        }
    }

    private static int createRatingFinish(double m, double a, double pct, double multiplier) {
        if(a > 0.2) {
            //adjust for shots made     average for a player is 6 made 2's per game
            double initialRating = Math.round(pct * multiplier);
            double adjustedRating = initialRating + (m - 6) * 3;
            if(a < 1.1)
                adjustedRating *= 0.7;
            if(adjustedRating < 25)
                return 25;
            else if(adjustedRating > 105)
                adjustedRating *= 0.87;
            else if(adjustedRating > 95)
                adjustedRating *= 0.93;

            return (int) adjustedRating;
        }
        else
            return 25;
    }

    private static int createRatingShot(double m, double a, double pct, double multiplier) {
        if(a > 0.2) {
            //adjust for shots made     average for a player is 2 made 3's per game
            double initialRating = Math.round(pct * multiplier);
            double adjustedRating = initialRating + (m - 2) * 5;
            if(a < 1.1)
                adjustedRating *= 0.7;
            if(adjustedRating < 25)
                return 25;
            else if(adjustedRating > 115)
                adjustedRating *= 0.83;
            else if(adjustedRating > 105)
                adjustedRating *= 0.88;
            else if(adjustedRating > 95)
                adjustedRating *= 0.93;

            return (int) adjustedRating;
        }
        else
            return 25;
    }

    private static int createTendencyShot(double mins, double att, double multiplier) {
        double attPerMin = att * 48 / mins;
        if(attPerMin > 0.2)
            return (int)Math.round(attPerMin * multiplier);
        else
            return 0;
    }

    private static int createRatingReb(double mins, double rebs, double multiplier) {
        double initialRating = (rebs * 48 / mins * multiplier);
        double adjustedRating = initialRating;
        if(adjustedRating > 100)
            adjustedRating *= 0.89;
        else if(adjustedRating > 90)
            adjustedRating *= 0.92;
        return (int) adjustedRating;

    }

    private static int createRatingPass(double mins, double asts, double multiplier) {
        double rating = (asts * 48 / mins * multiplier);
        if(rating > 99)
            rating = 99;
        return (int) rating;
    }

    private static int createRatingDef(double def, double multiplier) {
        return (int) (def * multiplier);
    }



}
