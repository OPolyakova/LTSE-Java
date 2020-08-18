package com.company;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.*;
import java.util.function.Function;
import java.lang.Integer;

class myExchange extends basicTrade {
    private boolean header = true;

    private final class trade {				//data struct for trade info
        int sec;
        String[] recTokens;

        trade(String rec) {
            recTokens = rec.split(",");
            String[] date_time = get(indexOf("time_stamp")).split(" ")[1].split(":");

            int num = 60*60;
            for (String i : date_time) {
                if(i.length() > 1 && num >= 1) {
                    sec += num * Integer.parseInt(i);
                    num /= 60;
                }
            }
        }

        Boolean isValid() {
            List<String> myList = new ArrayList<>(Arrays.asList(recTokens));
            return myList.indexOf("") == -1 && recTokens.length == 8;
        }

        String get(Integer i) {
            if (i < recTokens.length) return recTokens[i];
            return "";
        }
    }

    //output files
    private FileWriter outRejected;
    private FileWriter outValid;

    private Set<String> _firms = new HashSet<>();	    //list of firm names
    private Set<String> _symbols = new HashSet<>();     //list of symbols

    private Integer _invalidTrCount = 0;							        //invalid trades count
    private Map<String, Vector<trade>> _vtrades = new HashMap<>();	//valid trades collection per firm

    void finish() {
        try
        {
            System.out.println("Closing files....\n");
            outRejected.flush();
            outValid.flush();
            outRejected.close();
            outValid.close();
        }
        catch (IOException e)
        {
            System.out.println("ERROR Standard exception; " + e.getMessage());
        }
        System.out.println(_invalidTrCount + " rejected trades.");
        System.out.println("DONE.");
    }

    myExchange (String work_dir) {
        String outRej = work_dir + "rejected.txt";
        String outAcc = work_dir + "accepted.txt";

        try
        {
            System.out.println("Opening out files....\n");
            outRejected = new FileWriter(outRej);
            outValid = new FileWriter(outAcc);
        }
        catch (IOException e)
        {
            System.out.println("ERROR: Standard exception; " + e.getMessage() + ". Exiting... ");
            System.exit(-1);
        }
    }

    Boolean AddFirm(String firm) {
        _firms.add(firm);
        return true;
    }

    Boolean AddSymbol(String sym) {
        _symbols.add(sym);
        return true;
    }

    //print out set content
    private void show(Set<String> inSet) {
        for(String x : inSet)
            System.out.println(x);
    }

    //print out firms
    void showFirms() {
        System.out.println("\nFirms:");
        show(_firms);
    }

    //print out symbols
    void showSymbols() {
        System.out.println("\nSymbols:");
        show(_symbols);
    }

    //is symbol set
    private Boolean validSymbol(String s) {
        return _symbols.contains(s);
    }

    //is broker set
    private Boolean validBroker(String s) {
        return _firms.contains(s);
    }

    //--------------- get counts -----------------------
    Integer getFirmCount() {
        return _firms.size();
    }

    Integer getSymCount() {
        return _symbols.size();
    }

    Integer getInvalidTradesCount() {
        return _invalidTrCount;
    }

    Integer getValidTradesCount() {
        int sum = 0;
        for(List<trade> lt : _vtrades.values())
            sum += lt.size();
        return sum;
    }
    //-------------------------------------

    //check if trade rec based on condition is under max limit
    private Boolean isValidBkTradeLimit(String bk, Function<trade, Boolean> pred) {
        List<trade> bk_trades;

        if(_vtrades.containsKey(bk)) {
            bk_trades = _vtrades.get(bk);
            Collections.reverse(bk_trades);
        } else return true;

        int count = 0;
        for(trade tr : bk_trades ) {
            if (pred.apply(tr)) count++;
            if (count >= 3) return false;
        }
        return true;
    }

    //find if trade rec based on condition exists
    private Boolean findBkTrade(String bk, Function<trade, Boolean> pred) {
        List<trade> bk_trades;

        if(_vtrades.containsKey(bk)) {
            bk_trades = _vtrades.get(bk);
        } else return false;

        for(trade tr : bk_trades) {
            if (pred.apply(tr)) return true;
        }
        return false;
    }

    //is sequence unique for the broker
    private Boolean isValidNewSeq(String seq, String bk) {
        return !findBkTrade(bk, rec -> rec.get(indexOf("sequence_id")).equals(seq) );
    }

    //check trade rec for requirements
    private boolean isValidTrade(trade tr) {
        boolean valid = true;
        String sym = tr.get(indexOf("symbol"));
        String br = tr.get(indexOf("broker"));
        String sq = tr.get(indexOf("sequence_id"));

        if(!tr.isValid())  {
            System.out.println("WARNING: invalid record for broker " + br);
            valid = false;
        }

        if (!validSymbol(sym)) {
            System.out.println("WARNING: invalid sym " + sym + " for broker " + br);
            valid = false;
        }

        if (!validBroker(br)) {
            System.out.println("WARNING: invalid broker " + br);
            valid = false;
        }

        if(!isValidNewSeq(sq,br)) {
            System.out.println("WARNING: invalid seq " + sq + " for broker " + br);
            valid = false;
        }

        if(!isValidBkTradeLimit(br, rec -> rec.sec > tr.sec - 60)) {
            System.out.println("WARNING: invalid TIME " + tr.get(indexOf("time_stamp")).split(" ")[1] + " for broker " + br);
            valid = false;
        }
        return valid;
    }

    private static void writeToFile(String rec, FileWriter fw) {
        try
        {
            fw.write(rec);
        }
        catch (IOException e)
        {
            System.out.println("ERROR Standard exception; " + e.getMessage());
        }
    }

    //evaluate the trade record
    Boolean processTrade(String record) {
        if (header) {                       //ignore header
            header = false;
            writeToFile(record,outRejected);
            writeToFile(record,outValid);
            return true;
        }

        trade tr = new trade(record);
        String br = tr.get(indexOf("broker"));

        if(isValidTrade(tr)) {
            //add trade to collection
            Vector<trade> ltr;
            if (_vtrades.containsKey(br)) {
                ltr = _vtrades.get(br);
            } else {
                ltr = new Vector<>();
            }
            ltr.add(tr);
            _vtrades.put(br, ltr);

            writeToFile(record,outValid);
        } else {
            _invalidTrCount++;
            writeToFile(record,outRejected);
        }
        return true;
    }
}
