public class Player {

    //Attributes
    private int id, height, position, age;
    private String fName, lName, from, number;
    private int attFinish, attShot, attDef, attReb, attPass;
    private int tenDrive, tenThree, tenShot;
    //Game Data
    private int dataPts, dataRebs, dataAsts, dataStls;
    private int dataFGM, dataFGA, data3PM, data3PA;
    //Season Data
    private int seasonGames, seasonPts, seasonRebs, seasonAsts, seasonStls;
    private int seasonFGM, seasonFGA, season3PM, season3PA;
    //Season highs
    private int highPts, highRebs, highAsts, highStls, high3PM;
    //Career data
    private int careerGames, careerPts, careerRebs, careerAsts, careerStls;
    private int careerFGM, careerFGA, career3PM, career3PA;
    //Career highs
    private int careerHighPts, careerHighRebs, careerHighAsts, careerHighStls, careerHigh3PM;
    //Career accolades
    private int mvpCount, finalsMVPCount, championshipsCount;
    private boolean roty, allRookie;
    private int[] allNBA = {0,0,0};
    //other
    private int draftYear = 0;
    private boolean hasBall;
    private Team team;


    public Player(int id, String fn, String ln, String from, int h, String num, int f, int sh, int def, int reb, int pass, int tD, int t3, int tS, int age) {
        this.id = id;
        this.fName = fn;
        this.lName = ln;
        this.from = from;
        this.age = age;
        this.height = h;
        this.number = num;
        this.attFinish = f;
        this.attShot = sh;
        this.attDef = def;
        this.attReb = reb;
        this.attPass = pass;
        this.tenDrive = tD;
        this.tenThree = t3;
        this.tenShot = tS;
        this.dataPts = 0;
        this.dataRebs = 0;
        this.dataAsts = 0;
        this.dataStls = 0;
        this.dataFGM = 0;
        this.dataFGA = 0;
        this.data3PM = 0;
        this.data3PA = 0;
        this.mvpCount = 0;
        this.finalsMVPCount = 0;
        this.championshipsCount = 0;
        this.roty = false;
        this.allRookie = false;
    }

    public void addPts(int n) {dataPts += n;}
    public void addRebs() {dataRebs ++;}
    public void addAsts() {dataAsts ++;}
    public void addStls() {dataStls ++;}
    public void addFG(boolean made) {
        dataFGA++;
        if(made) {
            dataFGM++;
            addPts(2);
        }
    }
    public void add3PT(boolean made) {
        dataFGA++;
        data3PA++;
        if(made) {
            dataFGM++;
            data3PM++;
            addPts(3);
        }
    }

    private void addGame() {seasonGames++; careerGames++;}
    private void addSeasonPts(int n) {seasonPts += n; careerPts += n;}
    private void addSeasonRebs(int n) {seasonRebs += n; careerRebs += n;}
    private void addSeasonAsts(int n) {seasonAsts += n; careerAsts += n;}
    private void addSeasonStls(int n) {seasonStls += n; careerStls += n;}
    private void addSeasonFG(int m, int a) {
        seasonFGM += m;
        seasonFGA += a;
        careerFGM += m;
        careerFGA += a;
    }
    private void addSeason3PT(int m, int a) {
        season3PM += m;
        season3PA += a;
        career3PM += m;
        career3PA += a;
    }
    
    public void setBall() {
        hasBall = true;
        Main.getTeam(this).setPos();
    }
    public void setNoBall() {hasBall = false;}
    public void setPosition(int pos) {this.position = pos;}
    public void addAge() {age++;}

    public int getId() {return this.id;}
    public String getFrom() {return this.from;}
    public String getNumber() {return this.number;}
    public int getAge() {return this.age;}
    public int getHeight() {return this.height;}
    public int getAttFinish() {return this.attFinish;}
    public int getAttShot() {return this.attShot;}
    public int getAttReb() {return this.attReb;}
    public int getAttDef() {return this.attDef;}
    public int getAttPass() {return this.attPass;}

    public int getTenDrive() {return this.tenDrive;}
    public int getTenThree() {return this.tenThree;}
    public int getTenShot() {return this.tenShot;}

    public void changeAttribute(int att, int change) {
        switch(att) {
            case 0: attFinish += change; break; //finish
            case 1: attShot += change; break; //shoot
            case 2: attReb += change; break; //rebound
            case 3: attDef += change; break; //defense
            case 4: attPass += change; break; //pass
        }
    }

    public boolean getBall() {return this.hasBall;}
    public int getPosition() {return this.position;}
    public String getPositionStr() {
        switch (position) {
            case 0: return "PG";
            case 1: return "SG";
            case 2: return "SF";
            case 3: return "PF";
            case 4: return "C";
            default: return null;
        }
    }
    
    public int getPts() {return this.dataPts;}
    public int getRebs() {return this.dataRebs;}
    public int getAsts() {return this.dataAsts;}
    public int getStls() {return this.dataStls;}
    public int getFGM() {return this.dataFGM;}
    public int getFGA() {return this.dataFGA;}
    public int get3PM() {return this.data3PM;}
    public int get3PA() {return this.data3PA;}
    public int getFGpct() {return this.percentage(dataFGM, dataFGA);}
    public int get3Ppct() {return this.percentage(data3PM, data3PA);}

    public int getSeasonGames() {return this.seasonGames;}
    public int getSeasonPts() {return this.seasonPts;}
    public int getSeasonRebs() {return this.seasonRebs;}
    public int getSeasonAsts() {return this.seasonAsts;}
    public int getSeasonStls() {return this.seasonStls;}
    public int getSeasonFGM() {return this.seasonFGM;}
    public int getSeasonFGA() {return this.seasonFGA;}
    public int getSeason3PM() {return this.season3PM;}
    public int getSeason3PA() {return this.season3PA;}
    public int getSeasonFGpct() {return this.percentage(seasonFGM, seasonFGA);}
    public int getSeason3Ppct() {return this.percentage(season3PM, season3PA);}

    public int getCareerGames() {return this.careerGames;}
    public int getCareerPts() {return this.careerPts;}
    public int getCareerRebs() {return this.careerRebs;}
    public int getCareerAsts() {return this.careerAsts;}
    public int getCareerStls() {return this.careerStls;}
    public int getCareerFGM() {return this.careerFGM;}
    public int getCareerFGA() {return this.careerFGA;}
    public int getCareer3PM() {return this.career3PM;}
    public int getCareer3PA() {return this.career3PA;}
    public int getCareerFGpct() {return this.percentage(careerFGM, careerFGA);}
    public int getCareer3Ppct() {return this.percentage(career3PM, career3PA);}

    public int getHighPts() {return this.highPts;}
    public int getHighRebs() {return this.highRebs;}
    public int getHighAsts() {return this.highAsts;}
    public int getHighStls() {return this.highStls;}
    public int getHigh3PM() {return this.high3PM;}

    public int getCareerHighPts() {return this.careerHighPts;}
    public int getCareerHighRebs() {return this.careerHighRebs;}
    public int getCareerHighAsts() {return this.careerHighAsts;}
    public int getCareerHighStls() {return this.careerHighStls;}
    public int getCareerHigh3PM() {return this.careerHigh3PM;}

    public int getMvpCount() {return this.mvpCount;}
    public void addMvpCount() {mvpCount++;}

    public int getFinalsMVPCount() {return this.finalsMVPCount;}
    public void addFinalsMVPCount() {finalsMVPCount++;}

    public int getChampionshipsCount() {return this.championshipsCount;}
    public void addChampionshipsCount() {championshipsCount++;}

    public int[] getAllNBA() {return this.allNBA;}
    public void addAllNBA(int t) {
        switch (t) {
            case 1: allNBA[0]++; break;
            case 2: allNBA[1]++; break;
            case 3: allNBA[2]++; break;
            default: break;
        }
    }

    public boolean getRoty() {return this.roty;}
    public void addRoty() {roty = true;}

    public boolean getAllRookie() {return this.allRookie;}
    public void addAllRookie() {allRookie = true;}

    public int getDraftYear() {return this.draftYear;}
    public void setDraftYear(int dy) {this.draftYear = dy;}

    public void flushData() {
        this.dataPts = 0;
        this.dataRebs = 0;
        this.dataAsts = 0;
        this.dataStls = 0;
        this.dataFGM = 0;
        this.dataFGA = 0;
        this.data3PM = 0;
        this.data3PA = 0;
    }
    public void updateHighs() {
        if(dataPts > highPts)
            highPts = dataPts;
        if(dataRebs > highRebs)
            highRebs = dataRebs;
        if(dataAsts > highAsts)
            highAsts = dataAsts;
        if(dataStls > highStls)
            highStls = dataStls;
        if(data3PM > high3PM)
            high3PM = data3PM;

        if(dataPts > careerHighPts)
            careerHighPts = dataPts;
        if(dataRebs > careerHighRebs)
            careerHighRebs = dataRebs;
        if(dataAsts > careerHighAsts)
            careerHighAsts = dataAsts;
        if(dataStls > careerHighStls)
            careerHighStls = dataStls;
        if(data3PM > careerHigh3PM)
            careerHigh3PM = data3PM;
    }
    public void updateSeasonStats() {
        addGame();
        addSeasonPts(dataPts);
        addSeasonRebs(dataRebs);
        addSeasonAsts(dataAsts);
        addSeasonStls(dataStls);
        addSeasonFG(dataFGM, dataFGA);
        addSeason3PT(data3PM, data3PA);
    }

    public void zeroStats() {
        seasonGames = 0; seasonPts = 0; seasonRebs = 0; seasonAsts = 0; seasonStls = 0;
        seasonFGM = 0; seasonFGA = 0; season3PM = 0; season3PA = 0;
        highPts = 0; highRebs = 0; highAsts = 0; highStls = 0; high3PM = 0;
    }

    public void setTeam(Team t) { this.team = t; }
    public Team getTeam() { return this.team; }

    public void printInfo() {
        String result = "";
        result += String.format("[%s] %s %s || %s from %s%n", number, fName, lName, inchesToFeet(), from);
        result += String.format("-- Points: %d | Rebounds: %d | Assists: %d | Steals: %d%n", dataPts, dataRebs, dataAsts, dataStls);
        result += String.format("-- Field Goals: %d/%d - %d%% | 3-point Field Goals: %d/%d - %d%%%n", dataFGM, dataFGA, percentage(dataFGM, dataFGA), data3PM, data3PA, percentage(data3PM, data3PA));
        System.out.println(result);
    }

    public void printSeasonInfo() {
        String result = "";
        result += String.format("[%s] %s %s || %s from %s%n", number, fName, lName, inchesToFeet(), from);
        result += String.format("Team: %s%n", team);
        result += String.format("-- Points: %.1f | Rebounds: %.1f | Assists: %.1f | Steals: %.1f%n", calculatePerGame(1), calculatePerGame(2), calculatePerGame(3), calculatePerGame(4));
        result += String.format("-- Field Goals: %d/%d - %d%% | 3-point Field Goals: %d/%d - %d%%%n", seasonFGM, seasonFGA, percentage(seasonFGM, seasonFGA), season3PM, season3PA, percentage(season3PM, season3PA));
        System.out.println(result);
    }

    public String toString() {
        String result = "";
        result += String.format("%s %s", fName, lName);
        return result;
    }

    public String getLastName() {return this.lName;}

    public String toStringWithNumber() {
        return String.format("[%s] %s. %s", number, fName.substring(0, 1), lName);
    }

    public String prospectInfo() {
        return String.format("%s (%s %s): %d (%d/%d/%d/%d/%d)", this, inchesToFeet(), this.getPositionStr(), calculateOverall(), getAttFinish(), getAttShot(), getAttDef(), getAttReb(), getAttPass());
    }

    public String inchesToFeet() {
        return height/12 + "'" + height%12 + '"';
    }

    private int percentage(int top, int bottom) {
        if(top == 0 || bottom == 0)
            return 0;
        else return (top * 100) / bottom;
    }

    public int calculateEval() {
        return (dataPts + dataRebs + dataAsts + dataStls - (dataFGA - dataFGM));
    }

    public int calculateSeasonEval() {
        return (seasonPts + seasonRebs + seasonAsts + seasonStls/* - (seasonFGA - seasonFGM)*/);
    }

    public double calculatePerGame(int type) {
        double retVal = 0.0;
        // 0 - eval, 1 - points, 2 - rebs, 3 - assists, 4 - steals, 5 - 3pm
        switch(type) {
            case 0: retVal = (double) calculateSeasonEval() / seasonGames; break;
            case 1: retVal = (double) seasonPts / seasonGames; break;
            case 2: retVal = (double) seasonRebs / seasonGames; break;
            case 3: retVal = (double) seasonAsts / seasonGames; break;
            case 4: retVal = (double) seasonStls / seasonGames; break;
            case 5: retVal = (double) season3PM / seasonGames; break;
        }
        return retVal;
    }

    public int calculateOverall() {
        double overall = 0;
        double fin = attFinish;
        double sht = attShot;
        double def = attDef;
        double reb = attReb;
        double pas = attPass;
        switch(id % 5) {
            case 0: {
                double ovr1 = 0.1 * fin + 0.3 * sht + 0.3 * def + 0.3 * pas; //shooter
                double ovr2 = 0.3 * fin + 0.1 * sht + 0.3 * def + 0.3 * pas; //finisher
                double ovr3 = 0.15 * fin + 0.15 * sht + 0.2 * def + 0.5 * pas; //pass-first
                double ovr4 = 0.4 * fin + 0.4 * sht + 0.1 * def + 0.1 * pas; //pure scorer
                double ovr5 = 0.2 * fin + 0.2 * sht + 0.4 * def + 0.2 * pas; //defender
                overall = Math.max(ovr1, ovr2);
                overall = Math.max(overall, ovr3);
                overall = Math.max(overall, ovr4);
                overall = Math.max(overall, ovr5);
                break;
            }
            case 1: {
                double ovr1 = 0.2 * fin + 0.5 * sht + 0.3 * def; //shooter
                double ovr2 = 0.5 * fin + 0.2 * sht + 0.3 * def; //slasher
                double ovr3 = 0.25 * fin + 0.25 * sht + 0.5 * def; //defender
                double ovr4 = 0.2 * fin + 0.2 * sht + 0.3 * def + 0.3 * pas; //combo
                double ovr5 = 0.45 * fin + 0.45 * sht + 0.1 * def; //pure scorer
                overall = Math.max(ovr1, ovr2);
                overall = Math.max(overall, ovr3);
                overall = Math.max(overall, ovr4);
                overall = Math.max(overall, ovr5);
                break;
            }
            case 2: {
                double ovr1 = 0.3 * fin + 0.3 * sht + 0.3 * def + 0.1 * reb; //all-around
                double ovr2 = 0.4 * fin + 0.2 * sht + 0.3 * def + 0.1 * reb; //slasher
                double ovr3 = 0.2 * fin + 0.4 * sht + 0.3 * def + 0.1 * reb; //shooter
                double ovr4 = 0.2 * fin + 0.2 * sht + 0.3 * def + 0.3 * pas; //point-forward
                double ovr5 = 0.2 * fin + 0.2 * sht + 0.5 * def + 0.1 * reb; //defender
                double ovr6 = 0.4 * fin + 0.4 * sht + 0.1 * def + 0.1 * reb; //pure scorer
                overall = Math.max(ovr1, ovr2);
                overall = Math.max(overall, ovr3);
                overall = Math.max(overall, ovr4);
                overall = Math.max(overall, ovr5);
                overall = Math.max(overall, ovr6);
                break;
            }
            case 3: {
                double ovr1 = 0.4 * fin + 0.3 * def + 0.3 * reb; //finisher
                double ovr2 = 0.2 * fin + 0.4 * sht + 0.2 * def + 0.2 * reb; //stretch4
                double ovr3 = 0.2 * fin + 0.2 * sht + 0.3 * def + 0.3 * reb; //all-around
                double ovr4 = 0.1 * fin + 0.1 * sht + 0.5 * def + 0.3 * reb; //defender
                double ovr5 = 0.2 * fin + 0.1 * sht + 0.2 * def + 0.5 * reb; //rebounder
                overall = Math.max(ovr1, ovr2);
                overall = Math.max(overall, ovr3);
                overall = Math.max(overall, ovr4);
                overall = Math.max(overall, ovr5);
                break;
            }
            case 4: {
                double ovr1 = 0.4 * fin + 0.3 * def + 0.3 * reb; //inside
                double ovr2 = 0.2 * fin + 0.4 * sht + 0.2 * def + 0.2 * reb; //stretch-five
                double ovr3 = 0.2 * fin + 0.4 * def + 0.4 * reb; //def&reb
                overall = Math.max(ovr1, ovr2);
                overall = Math.max(overall, ovr3);
                overall *= 0.93;
                break;
            }
        }
        return (int) overall;
    }
}
