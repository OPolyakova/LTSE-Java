package com.company;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.function.Function;

public class Main {

    //read file and apply appropriate function
     private static void readFromFile(String name, Function<String, Boolean> func) {
        try {
            FileReader inF = new FileReader(name);
            Scanner myReader = new Scanner(inF);
            while (myReader.hasNextLine()) {
                String Line = myReader.nextLine();
                func.apply(Line.trim());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR Standard exception: " + e.getMessage() + ". Exiting... ");
            System.exit(-1);
        }

    }

    private static void testEx(String dir) {
        System.out.println("Running test\n");
        myExchange myTestEx = new myExchange(dir);
         //setup

        myTestEx.AddFirm("Fidelity");
        myTestEx.AddFirm("Fidelity");
        myTestEx.AddFirm("Charles Schwab");
        myTestEx.AddFirm("Ameriprise Financial");
        myTestEx.AddFirm("TD Ameritrade");
        myTestEx.AddFirm("Fidelity");

        myTestEx.AddSymbol("BARK");
        myTestEx.AddSymbol("BARK");
        myTestEx.AddSymbol("CARD");
        myTestEx.AddSymbol("HOOF");
        myTestEx.AddSymbol("LOUD");
        myTestEx.AddSymbol("BARK");

        //Run
        myTestEx.processTrade("Time stamp,broker,sequence id,type,Symbol,Quantity,Price,Side");

        myTestEx.processTrade("10/5/2017 10:00:11,TD Ameritrade,1,K,LOUD,200,10.95,Buy");			// valid #1
        myTestEx.processTrade("10/5/2017 10:00:11,TD Ameritrade,1,K,LOUD,200,10.95,Buy");			// invalid seq
        myTestEx.processTrade("10/5/2017 10:00:12,TD Ameritrade,2,K,LOUD,200,10.95,Buy");			// valid #2
        myTestEx.processTrade("10/5/2017 10:00:40,Fidelity,2,2,LGHT,100,140.05,Buy");				//invalid sym
        myTestEx.processTrade("10/5/2017 10:00:41,Fidelity,3,K,BARK,100,1.19,Buy");				// valid #3
        myTestEx.processTrade("10/5/2017 10:00:41,Fidelity,4,K,BARK,100,1.19,Buy");				// valid #4
        myTestEx.processTrade("10/5/2017 10:00:41,Fidelity,5,K,,100,1.19,Buy");					//missing field
        myTestEx.processTrade("10/5/2017 10:00:41,TD Ameritrade,3,K,LOUD,200,10.95,Buy");			// valid #5
        myTestEx.processTrade("10/5/2017 10:00:42,TD Ameritrade,4,K,LOUD,200,10.95,Buy"); 			// > 3 per min
        myTestEx.processTrade("10/5/2017 10:00:42,Charles Schwab,2,2,DARK,100,20,Buy");			//invalid sym
        myTestEx.processTrade("10/5/2017 10:00:42,Charles Schwab,3,K,HOOF,100,39.11,Buy");			// valid #6
        myTestEx.processTrade("10/5/2017 10:00:43,Edward Jones,2,2,BARK,400,88.3,Buy"); 			//invalid broker
        myTestEx.processTrade("10/5/2017 10:00:43,TD Ameritrade,5,2,GLOO,500,40.01,Sell");			// > 3 per min
        myTestEx.processTrade("10/5/2017 10:00:44,Ameriprise Financial,2,K,LGHT,800,98.765,Sell"); //invalid sym
        myTestEx.processTrade("10/5/2017 10:00:44,Ameriprise Financial,3,2,LOUD,400,10.96,Sell");	// valid #7
        myTestEx.processTrade("10/5/2017 10:00:45,Ameriprise Financial,4,2,LOUD,400,10.96,Sell");	// valid #8
        myTestEx.processTrade("10/5/2017 10:00:46,Ameriprise Financial,5,2,LOUD,400,10.96,Sell");	// valid #9
        myTestEx.processTrade("10/5/2017 10:00:47,Ameriprise Financial,6,2,LOUD,400,10.96,Sell");	// > 3 per min
        myTestEx.processTrade("10/5/2017 10:00:48,Ameriprise Financial,7,2,LOUD,400,10.96,Sell");	// > 3 per min
        myTestEx.processTrade("10/5/2017 10:00:49,Ameriprise Financial,8,2,LOUD,400,10.96,Sell");	// > 3 per min

        //Evalute
        System.out.println("Statistics:\n");
        myTestEx.showFirms();
        myTestEx.showSymbols();

        System.out.println( "\nFirms count: " + myTestEx.getFirmCount());
        assert(myTestEx.getFirmCount() == 4);

        System.out.println("Symbols count: " + myTestEx.getSymCount());
        assert(myTestEx.getSymCount() == 4);

        System.out.println("Invalid Trades count: " + myTestEx.getInvalidTradesCount());
        assert(myTestEx.getInvalidTradesCount() == 11);


        System.out.println("Valid Trades count: " + myTestEx.getValidTradesCount());
        System.out.println("Total Trades count: " + (myTestEx.getValidTradesCount() + myTestEx.getInvalidTradesCount()));

        assert(myTestEx.getValidTradesCount() == 9); 									//valid trades count
        assert(myTestEx.getValidTradesCount() + myTestEx.getInvalidTradesCount() == 20);	//total trades count

        myTestEx.finish();
        System.out.println("Test finished. \n\n");
    }

    public static void main(String[] args) {
        System.out.println("Welcome to LTSE!\n");

        //the following could be input params
        String dir = "C:\\Olya\\LTSE\\";
        boolean runTest = false;                        //set to true to run the test

        String inFirms = dir + "firms.txt";
        String inSymb = dir + "symbols.txt";
        String inTrades = dir + "trades.csv";

        if(runTest)
            testEx(dir);
        else {
            myExchange myEx = new myExchange(dir);

            //Load firms
            readFromFile(inFirms, myEx::AddFirm);

            //Load symbols
            readFromFile(inSymb, myEx::AddSymbol);

            //Process trades
            readFromFile(inTrades, myEx::processTrade);

            myEx.finish();
        }
    }
}
