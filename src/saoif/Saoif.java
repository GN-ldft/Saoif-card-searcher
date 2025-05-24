package saoif;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Windows
 */
public class Saoif {
    
    public static int MAXCARDNUM;
    static{
        try{
            File cardNumFile = new File("MAXCARDNUM.txt");
            Scanner cardNumReader = new Scanner(cardNumFile);
            MAXCARDNUM = cardNumReader.nextInt();
        }catch(Exception e){
            System.out.println("Fail to read \"MAXCARDNUM.txt\", that should only contain one integer.\n"
                    + "Input properly and try again. \n"
                    + "Bye");
            System.exit(0);
        }
    }
    public static Card[] cards = new Card[MAXCARDNUM];

    public static void buildCards()throws Exception {
        File f = new File("cards.txt");
        Scanner s = new Scanner(f);
        for (int i=0; i<MAXCARDNUM; i++){
            cards[i] = new Card(s.nextLine());
        }
    }
    
    public static void saveCards(String fileName) throws Exception {
       PrintStream out = new PrintStream(System.out);
       PrintStream file = new PrintStream(fileName);
       Scanner in = new Scanner(System.in);
       
       out.println("start : ");
       int start = in.nextInt();
       out.println("end : ");
       int end = in.nextInt();
       Card.addCardsToFile(start, end, file);
    }
    
    public static void saveWanted(int[] targetCardsNumber) throws Exception {
       PrintStream out = new PrintStream(System.out);
       PrintStream file = new PrintStream("save.txt");
       Scanner in = new Scanner(System.in);
       
        String[] na = {""};
        String[] typeTrans = Card.htmlTrans(na, "typetrans");
       
       for (int i=0; !(targetCardsNumber[i]==0); i++){
            file.printf("%d %s %s %s %s %s", cards[targetCardsNumber[i]-1].num, cards[targetCardsNumber[i]-1].name, cards[targetCardsNumber[i]-1].title, cards[targetCardsNumber[i]-1].weap, cards[targetCardsNumber[i]-1].weapEle, cards[targetCardsNumber[i]-1].ele);
            for (int j = 0; j < cards[targetCardsNumber[i]-1].type.length - 1; j++) {
                if (cards[targetCardsNumber[i]-1].type[j] == 1) {
                    file.printf(" %s", typeTrans[j]);
                }
            }
            file.printf(" %s", cards[targetCardsNumber[i]-1].buffs);
            file.println();
       }
    }
    
    public static void deleteWanted(){
        FileDownloader.delete("./wanted_cards");
    }
    
    public static void getWanted(String[] tar)throws Exception {
       int[] wanted = Card.search(cards,tar);
       //saveWanted(wanted);
       System.out.println("Wanted cards: ");
       for (int i=0; i<Card.realLenNums(wanted); i++){
           Card target = cards[wanted[i]-1];
           target.cardPrint();
           Card.movePic(target);
           //target.cardPrint();
       }
    }
    
    public static void showImagesInJOptionPane(File[] pics, int row, int column) {
        // Create a panel to hold the images
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(row, column)); // 2 rows and 4 columns

        // Load and add images to the panel
        for (File pic : pics) {
            try {
                BufferedImage img = ImageIO.read(pic);
                ImageIcon icon = new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH)); // Scale images
                JLabel label = new JLabel(icon);
                panel.add(label);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading image: " + pic.getName());
            }
        }

        // Show the images in a JOptionPane
        if (row==1){
            JOptionPane.showMessageDialog(null, panel, "Slot", JOptionPane.INFORMATION_MESSAGE);
        }
        if (row==2){
            JOptionPane.showMessageDialog(null, panel, "Set", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public static String[] buildTar(){
        PrintStream p = new PrintStream(System.out);
        Scanner s = new Scanner(System.in);
        p.println("Input the conditions (if end, type \"end\", banners: \"banners/123\"): ");
        String[] tar = new String[6];
        String tmp;
        
        while (Card.realLen(tar)<6){
            tmp = s.nextLine();
            if (tmp.equals("end")){
                break;
            }
            tar[Card.realLen(tar)] = tmp;
        }
        
        for (int i=Card.realLen(tar); i<tar.length; i++){
            tar[i] = "";
        }
        
        return tar;
    }
    
    public static void buildDeck(){
        PrintStream p = new PrintStream(System.out);
        Scanner s = new Scanner(System.in);
        p.println("Deck with 8 cards or slot with 3 cards (3/8): ");
        int max;
        try{max = s.nextInt();}catch(Exception e){return;}
        if (!(max==3||max==8)){return;}
        p.println("Input cards number (Skills first):");
        int[] numbers = new int[max];
        File[] pics = new File[max];
        
        for (int i=0; i<max; i++){
            numbers[i] = s.nextInt();
            pics[i] = cards[numbers[i]-1].pic;
        }
        if (max ==3){
            showImagesInJOptionPane(pics, 1,3);
        }else{
            File tmp;
            tmp=pics[2]; pics[2]=pics[4];pics[4]=tmp;
            tmp=pics[3]; pics[3]=pics[5];pics[5]=tmp;
            showImagesInJOptionPane(pics, 2, 4);
        }
    }
    
    public static void add(String[] tmp, String input){
        if (Card.realLen(tmp)==tmp.length){
            for (int i=0; i<tmp.length-1; i++){
                tmp[i] = tmp[i+1];
            }
            tmp[tmp.length-1] = input;
        }else{
            tmp[Card.realLen(tmp)] = input;
        }
    }
    
    public static void inputBanner(){
        String[] inputs = new String[MAXCARDNUM];
        int noOfCards;
        String tmp;
        System.out.println("Input Titles of Cards(\"end\" if end): ");
        Scanner s = new Scanner(System.in);
        for (noOfCards = 0; noOfCards<MAXCARDNUM; noOfCards++){
            tmp = s.nextLine();
            if (tmp.equals("end")){
                break;
            }
            inputs[noOfCards] = tmp;
        }
        String[] titles = Card.simpArray(inputs);
        
        //String[] titles = 
        
        int[] cardsNum = new int[titles.length];
        for (int i=0; i<cardsNum.length; i++){
            for(int j=0; j<MAXCARDNUM; j++){
                if(cards[j].title.contains(titles[i])){
                    cardsNum[i] = cards[j].num;
                    break;
                }
            }
        }
        Card target;
        for (int i=0; i<cardsNum.length; i++){
            try{
                target = cards[cardsNum[i]-1];
                target.cardPrint();
                Card.movePic(target);
            }catch(Exception e){
                System.out.println(titles[i]+" error");
            }
        }
        System.out.println("END");
    }
    
    public static void bannerFile()throws Exception {
        String[] titles = Card.getBanner("banner.txt");
        
        int[] cardsNum = new int[titles.length];
        for (int i=0; i<cardsNum.length; i++){
            for(int j=0; j<MAXCARDNUM; j++){
                if(cards[j].title.contains(titles[i])){
                    cardsNum[i] = cards[j].num;
                    break;
                }
            }
        }
        Card target;
        for (int i=0; i<cardsNum.length; i++){
            try{
                target = cards[cardsNum[i]-1];
                target.cardPrint();
                Card.movePic(target);
            }catch(Exception e){
                System.out.println(titles[i]+" error");
            }
        }
        System.out.println("END");
    }
    
    public static void update() throws Exception {
        System.out.println("Please go to \"https://saoif.fanadata.com/characters\"");
        System.out.println("Check what is the id of newest card");
        System.out.println("e.g. the link of newest card is https://saoif.fanadata.com/characters/1480, then input 1480.");
        System.out.printf("input: ");
        Scanner in = new Scanner(System.in);
        int maxCardNum = in.nextInt();
        PrintStream o = new PrintStream(System.out);
        int start = 1;
        
        o.printf("Input \"Y\" to sure that you want to delete and download all files in /web and /pic ? ");
        String dum = in.next();
        if (!(dum.equals("Y"))){
            try{
                o.printf("Input \"Y\" to sure that you want to download files start from no.%d ? ", MAXCARDNUM+1);
                dum = in.next();
                if (!(dum.equals("Y"))){
                    throw new Exception();
                }
                start = MAXCARDNUM+1;
            }catch(Exception e){
                o.println("Cancelled update...");
                return;
            }
        }
        o.println("---------------Start updating--------------");
        
        PrintStream numFile = new PrintStream(new File("MAXCARDNUM.txt"));
        numFile.println(maxCardNum);
        MAXCARDNUM = maxCardNum;
        o.println("************MAXCARDNUM updated*************");
        
        FileDownloader imageDownloader = new FileDownloader(start,MAXCARDNUM, "pic");
        imageDownloader.download();
        o.println("************All pictures updated*************");
        
        FileDownloader webDownloader = new FileDownloader(start,MAXCARDNUM, "web");
        webDownloader.download();
        o.println("***************All webs updated**************");
        
        System.gc();
        o.printf("I am lazy to change the code, so just enter: %d, %d\n", 1, MAXCARDNUM);
        saveCards("cards.txt");
        
        System.gc();
        
        o.println("***************All cards saved**************");
        
        try{
            buildCards();
            o.println("Update successed, please restart the program\n");
            o.println("Restart the whole terminal or IDE");
        }catch(Exception e){
            o.println("Please restart the program");
        }
    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args)throws Exception {
       //test area
       //buildCards();
       //saveCards("cards.txt");System.exit(0);
       //FileDownloader down = new FileDownloader();down.download();System.exit(0);
       
       PrintStream out = new PrintStream(System.out);
       Scanner in = new Scanner(System.in);
       System.getProperty("file.encoding");
       Charset.defaultCharset();
       System.getProperty("java.version");
       System.setOut(new PrintStream(System.out, true, "UTF8"));
       
        try{
            buildCards();
        }catch(Exception e){
            update();
            System.exit(0);
        }
        
       String[] tmp = new String[8];
       
       String helpMessage = 
               "------------Saoif tool------------\n"
               + "0) \"exit\": end the program\n" 
               + "1) cards number to get info.\n"
               + "2) \"s\": search for cards\n"
               + "3) \"d\": build a set\n"
               + "4) \"c\": clear /wanted_cards\n"
               + "5) \"h\": show this again\n"
               + "6) string to notes, \"n\" to print\n"
               + "7) \"save\": input card nums to save card info into save.txt\n"
               + "8) \"banner\": input card titles and print details\n"
               + "9) \"banner.txt\": read all titles in the txt and run \"banner\"\n"
               + "10) \"update\": update the web and pic files\n"
               + "11) \"buffList\": get the entire bufflist";
       
       out.println(helpMessage);
       String input;
       while(true){
           input = in.nextLine();
           if (input.equals("s")){
               getWanted(buildTar());
               out.println("finished");
               continue;
           }
           if (input.equals("d")){
               buildDeck();
               out.println("built");
               continue;
           }
           if (input.equals("c")){
               deleteWanted();
               out.println("cleared");
               continue;
           }
           if (input.equals("n")){
               out.println("notes:");
               for(int i=0; i<Card.realLen(tmp);i++ ){
                   out.printf("%s ", tmp[i]);
               }
               out.println();
               continue;
           }
           if (input.equals("banner")){
               inputBanner();
               continue;
           }
           if (input.equals("banner.txt")){
               bannerFile();
               continue;
           }
           if (input.equals("h")){
                out.println(helpMessage);
                continue;
           }
           if (input.equals("buffList")){
               File file = new File("buffList.txt");
               PrintStream f = new PrintStream(file);
               f.println(Card.buffList);
               continue;
           }
           if (input.equals("save")){
               int[] targetCardsNumber = new int[MAXCARDNUM];
                int answer;
       
       
                for (int i=0; i<targetCardsNumber.length; i++){
                    out.printf("card number (out of range to end): ");
                    answer = in.nextInt();
            
                    if (answer<1||answer>MAXCARDNUM){
                        break;
                    }
                    targetCardsNumber[i] = answer;
                }
               saveWanted(targetCardsNumber);
               out.println("done");
               continue;
           }
           if (input.equals("update")){
               update();
           }
           if (input.equals("exit")){System.exit(0);}
           try{
               int num = Integer.parseInt(input);
               cards[num-1].cardPrint();
           }catch(Exception e){
               add(tmp, input);
           }
           
       }
       
    }
    
}
