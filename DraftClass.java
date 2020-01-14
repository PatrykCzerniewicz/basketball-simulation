import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class DraftClass {

    private ArrayList<String> firstNames = new ArrayList<>();
    private ArrayList<String> lastNames = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();

    public DraftClass() throws FileNotFoundException {
        createPlayers();
    }

    private void createNames() throws FileNotFoundException {
        File players = new File("players.txt");
        Scanner playersIn = new Scanner(players);
        while(playersIn.hasNextLine()) {
            String line = playersIn.nextLine();
            String[] spl = line.split(",");
            String fn = spl[1];
            String ln = spl[2];
            if(fn.contains("-")) {
                String[] fnSpl = fn.split("-");
                fn = fnSpl[0];
                firstNames.add(fnSpl[1]);
            }
            if(ln.contains(" ")) {
                String[] lnSpl = ln.split(" ");
                ln = lnSpl[0];
            }
            if(ln.contains("-")) {
                String[] lnSpl = ln.split("-");
                ln = lnSpl[0];
                lastNames.add(lnSpl[1]);
            }
            firstNames.add(fn);
            lastNames.add(ln);
        }
        playersIn.close();
    }

    private void createPlayers() throws FileNotFoundException {
        createNames();
        Random rnd = new Random();
        for(int i = 0; i < 45; i++) {
            int id = 1150 + (SimulateGame.getDraftsNumber() * 45) + i;
            String fn = firstNames.get(rnd.nextInt(firstNames.size()));
            String ln = lastNames.get(rnd.nextInt(lastNames.size()));
            String from = "TBD";
            String num = "" + rnd.nextInt(100);
            int age = rnd.nextInt(4) + 19;
            int h = 0, f = 0, sh = 0, def = 0, reb = 0, pass = 0, tD = 0, t3 = 0, tS = 0;
            int position = rnd.nextInt(5);
            if(position == 0) {
                //pg
                h = rnd.nextInt(10) + 69;
                f = rnd.nextInt(46) + 40;
                sh = rnd.nextInt(51) + 40;
                def = rnd.nextInt(26) + 65;
                reb = rnd.nextInt(41) + 10 + (h - 69);
                pass = rnd.nextInt(61) + 30;
            }
            else if(position == 1) {
                //sg
                h = rnd.nextInt(7) + 74;
                f = rnd.nextInt(46) + 40;
                sh = rnd.nextInt(51) + 40;
                def = rnd.nextInt(26) + 65;
                reb = rnd.nextInt(41) + 15 + (h - 69);
                pass = rnd.nextInt(51) + 20;
            }
            else if(position == 2) {
                //sf
                h = rnd.nextInt(6) + 77;
                f = rnd.nextInt(41) + 45;
                sh = rnd.nextInt(56) + 30;
                def = rnd.nextInt(26) + 65;
                reb = rnd.nextInt(41) + 25 + (h - 69);
                pass = rnd.nextInt(41) + 20;
            }
            else if(position == 3) {
                //pf
                h = rnd.nextInt(6) + 79;
                f = rnd.nextInt(31) + 55;
                sh = rnd.nextInt(51) + 30;
                def = rnd.nextInt(26) + 65;
                reb = rnd.nextInt(41) + 35 + (h - 69);
                pass = rnd.nextInt(31) + 15;
            }
            else if(position == 4) {
                //c
                h = rnd.nextInt(7) + 81;
                f = rnd.nextInt(26) + 60;
                sh = rnd.nextInt(56) + 25;
                def = rnd.nextInt(26) + 65;
                reb = rnd.nextInt(41) + 40 + (h - 69);
                pass = rnd.nextInt(26) + 10;
            }
            tD = f + rnd.nextInt(41) - 20;
            t3 = sh + rnd.nextInt(41) - 20;
            tS = (f + sh) / 2 + rnd.nextInt(41) - 20;
            Player p = new Player(id, fn, ln, from, h, num, f, sh, def, reb, pass, tD, t3, tS, age);
            p.setPosition(position);
            p.setDraftYear(SimulateGame.year);
            players.add(p);
        }
    }

    public ArrayList<Player> sorted() {
        ArrayList<Player> sorted = new ArrayList<>();
        ArrayList<Integer[]> ovrSorted = new ArrayList<>();
        for(Player p : players) {
            Integer[] player = {p.getId(), p.calculateOverall()};
            ovrSorted.add(player);
        }
        quickSort(ovrSorted, 0, ovrSorted.size() - 1, 1);
        Collections.reverse(ovrSorted);
        for(Integer[] player : ovrSorted) {
            Player p = findById(player[0]);
            sorted.add(p);
        }
        return sorted;
    }

    public void printDraftClass() {
        ArrayList<Player> sorted = sorted();
        for(Player p : sorted)
            System.out.println(p.prospectInfo());
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    public Player findById(int id) {
        for(Player p : players) {
            if(id == p.getId())
                return p;
        }
        return null;
    }

    private void quickSort(ArrayList<Integer[]> arr, int low, int high, int index)
    {
        //check for empty or null array
        if (arr == null || arr.size() == 0){
            return;
        }

        if (low >= high){
            return;
        }

        //Get the pivot element from the middle of the list
        int middle = low + (high - low) / 2;
        int pivot = arr.get(middle)[index];

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j)
        {
            //Check until all values on left side array are lower than pivot
            while (arr.get(i)[index] < pivot)
            {
                i++;
            }
            //Check until all values on left side array are greater than pivot
            while (arr.get(j)[index] > pivot)
            {
                j--;
            }
            //Now compare values from both side of lists to see if they need swapping
            //After swapping move the iterator on both lists
            if (i <= j)
            {
                swap (arr, i, j);
                i++;
                j--;
            }
        }
        //Do same operation as above recursively to sort two sub arrays
        if (low < j){
            quickSort(arr, low, j, index);
        }
        if (high > i){
            quickSort(arr, i, high, index);
        }
    }

    private void swap (ArrayList<Integer[]> array, int x, int y)
    {
        Integer[] temp = array.get(x);
        array.set(x, array.get(y));
        array.set(y, temp);
    }

}
