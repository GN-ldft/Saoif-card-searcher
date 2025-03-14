package saoif;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author
 */
//"character":{"id":1418,"person":{"id":5,"name_en":"Lisbeth","name_ja":"リズベット","name_ko":"리즈벳","name_tw":"莉茲貝特","gender":2,"priority":2},"element_type":null,"weapon_type":null,"attack_type":null,"name_en":"[Dangerous Scheme]","name_ja":"【危険な企て】","name_ko":"[위험한 계획]","name_tw":"【危險的計劃】"
//"name_tw":"莉茲貝特", "element_type":"Sword", "weapon_type":null, "attack_type":null, "name_tw":"【危險的計劃】
//nameInHtml[i] = "\">".concat(nameList[i]).concat(" <!-- -->【");
public class Card {

    public int num;
    public String name, weap, weapEle, ele;
    public int[] type = {0, 0, 0, 0, 0, 1}; // rush,mod,conn,acce,burs,null
    public static String[] target = new String[6];
    public static String[] targetHtml = new String[6];
    public File pic, web;
    public boolean match = false;
    public String title;
    public static String[] titleList = new String[Saoif.MAXCARDNUM];
    public static String[] allNames = new String[Saoif.MAXCARDNUM];
    public String buffs;
    public static String buffList;

    public Card(String cardData) {
        Scanner data = new Scanner(cardData);
        num = data.nextInt();
        name = data.next();
        title = data.next();
        weap = data.next();
        weapEle = data.next();
        ele = data.next();
        for (int i = 0; i < type.length; i++) {
            type[i] = data.nextInt();
        }
        buffs = data.next();
        if (!(buffs.equals("null"))){
            if (buffList==null){
                buffList = buffs;
            }else{
                buffList += buffs;
            }
        }
        pic = new File("pic".concat(File.separator).concat(String.valueOf(num)).concat(".jpg"));
        titleList[realLen(titleList)] = title;
        addIfUnique(allNames, name);
        //System.out.println(num);
    }

    public String cardString() {
        return String.valueOf(num).concat(" " + name + " " + title + " " + weap + " " + weapEle + " " + ele + " " + String.valueOf(type[0]) + " " + String.valueOf(type[1]) + " " + String.valueOf(type[2]) + " " + String.valueOf(type[3]) + " " + String.valueOf(type[4]) + " " + String.valueOf(type[5])+" "+buffs);
    }

    public void cardPrint() {
        String[] na = {""};
        String[] typeTrans = htmlTrans(na, "typetrans");
        System.out.printf("%d %s %s %s %s %s", num, name, title, weap, weapEle, ele);
        for (int i = 0; i < type.length - 1; i++) {
            if (type[i] == 1) {
                System.out.printf(" %s", typeTrans[i]);
            }
        }
        System.out.printf(" %s", buffs);
        System.out.println();
    }

    public Card(int number) throws Exception {
        num = number;
        String[] na = {""};
        File file = new File("web".concat(File.separator).concat(String.valueOf(number)).concat(".html"));
        Scanner s = new Scanner(file);
        String data = s.nextLine();
        pic = new File("pic".concat(File.separator).concat(String.valueOf(num)).concat(".jpg"));
        String[] nameInHtml = htmlTrans(na, "nameList");
        String[] typeList = htmlTrans(na, "typeList");
        String[] weapList = htmlTrans(na, "weapList");
        String[] eleList = htmlTrans(na, "eleList");
        String[] weapEleList = htmlTrans(na, "weapEleList");

        for (int i = 0; i < typeList.length; i++) {
            if (data.contains(typeList[i])) {
                type[i] = 1;
                type[typeList.length] = 0;
            } else {
                type[i] = 0;
            }
        }

        this.name = find(nameInHtml, data, 10);
        this.weap = find(weapList, data, 10);
        this.ele = find(eleList, data, 10);
        this.weapEle = find(weapEleList, data, 10);

        if (name == null) {
            name = "其他";
        }
        if (weap == null) {
            weap = "Ability";
        }
        if (ele == null) {
            ele = "null";
        }
        if (weapEle == null) {
            weapEle = "null";
        }

        if (weap.equals("Ability")) {
            ele = "null";
            weapEle = "null";
        }
        String[] dummy = {weap, name};
        String[] dum = htmlTrans(dummy, "");
        weap = dum[0];
        name = dum[1];

        if (!(findTitle(data) == null)) {
            title = findTitle(data)[1];
            String tmp = findTitle(data)[0];
            if (title == null) {
                title = "null";
            }
            title = title.replace(" ", "_");

            if (tmp != null && !tmp.equals(name)) {
                name = tmp.replace(" ", "_");
            }
        }
        buffs = getBuffs(data);
        if (buffs.equals("")){
            buffs = "null";
        }

        System.out.println("Card " + num + " scanned.");
    }
    
    

    public static int realLen(String[] arr) {
        int sigLength = arr.length;
        for (String arr1 : arr) {
            if (arr1 == null) {
                sigLength--;
            }
        }
        return sigLength;
    }

    public static int realLenNums(int[] arr) {
        int sigLength = arr.length;
        for (int arr1 : arr) {
            if (arr1 == 0) {
                sigLength--;
            }
        }
        return sigLength;
    }

    public static String find(String[] tar, String str, int repeatTimes) {

        double[] pos = new double[repeatTimes];
        String[] string = new String[repeatTimes];

        for (int i = 0; i < tar.length; i++) {
            if (str.contains(tar[i])) {
                pos[realLen(string)] = str.indexOf(tar[i]);
                string[realLen(string)] = tar[i];
                if (realLen(string) == string.length) {
                    break;
                }
            }
        }
        if (indexOfSmallest(pos) == -1) {
            return null;
        } else {
            return string[indexOfSmallest(pos)];
        }
    }

    public static int indexOfSmallest(double[] arr) {
        int minPos = -1;
        double tmp = 999999999;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < tmp && !(arr[i] == 0)) {
                minPos = i;
                tmp = arr[i];
            }
        }
        return minPos;
    }

    public static void printCardTo(Card card, PrintStream file) throws Exception {
        file.println(card.cardString());
    }

    public static void addCardsToFile(int start, int end, PrintStream p) throws Exception {
        PrintStream out = p;
        for (int i = start; i <= end; i++) {
            Card c = new Card(i);
            printCardTo(c, out);
        }
        System.gc();
    }

    public static String[] merge(String[] a, String[] b, String[] c, String[] d, String[] e) {
        String[] result = new String[a.length + b.length + c.length + d.length + e.length];
        int counter = 0;
        for (int i = 0; i < a.length; i++, counter++) {
            result[counter] = a[i];
        }
        for (int i = 0; i < b.length; i++, counter++) {
            result[counter] = b[i];
        }
        for (int i = 0; i < c.length; i++, counter++) {
            result[counter] = c[i];
        }
        for (int i = 0; i < d.length; i++, counter++) {
            result[counter] = d[i];
        }
        for (int i = 0; i < e.length; i++, counter++) {
            result[counter] = e[i];
        }

        return result;
    }

    //getting array or translating array
    public static String[] htmlTrans(String[] original, String type) {
        String[] result = new String[original.length];
        String[] nametrans = {"桐人", "亞絲娜", "莉茲貝特", "克萊因", "莉法", "詩乃", "西莉卡", "艾基爾", "有紀", "結衣", "幸", "尤吉歐", "愛麗絲", "小春", "亞魯戈", "斯朵蕾雅", "菲莉亞", "由奈", "伊迪絲", "伶茵", "桑妮雅", "希茲克利夫", "迪亞貝爾", "米特", "牙王", "鸚鵡螺", "貝爾庫利", "法那提歐", "瑪岱", "基滋梅爾", "史提菈", "圖風", "瑛二", "盧爾", "伊斯卡恩", "謝逹", "艾爾多利耶"};
        String[] typeList = {"突擊", "▲", ",\"data\":{\"connect\":[{\"id\":", "加速技能", "爆發"};
        String[] weapList = {"\"weapon_type\":\"Sword\"", "\"weapon_type\":\"Rapier\"", "\"weapon_type\":\"Dagger\"", "\"weapon_type\":\"Axe\"", "\"weapon_type\":\"Club\"", "\"weapon_type\":\"Bow\"", "\"weapon_type\":\"Spear\"", "\"weapon_type\":\"Shield\""};
        String[] eleList = {"Fire", "Water", "Wind", "Holy", "Earth", "Dark", "Void"};
        String[] weapEleList = {"Slash", "Thrust", "Blunt"};
        String[] nameList = new String[nametrans.length];
        for (int i = 0; i < nametrans.length; i++) {
            nameList[i] = "\">".concat(nametrans[i]).concat(" <!-- -->");
        }
        String[] typetrans = {"覺醒", "MOD", "連結", "加速", "爆發"};
        String[] weaptrans = {"Sword", "Rapier", "Dagger", "Axe", "Club", "Bow", "Spear", "Shield"};
        String[] eletrans = eleList;
        String[] weapEletrans = weapEleList;

        if (type.equals("nameList")) {
            return nameList;
        }
        if (type.equals("typeList")) {
            return typeList;
        }
        if (type.equals("weapList")) {
            return weapList;
        }
        if (type.equals("eleList")) {
            return eleList;
        }
        if (type.equals("weapEleList")) {
            return weapEleList;
        }
        if (type.equals("nametrans")) {
            return nametrans;
        }
        if (type.equals("typetrans")) {
            return typetrans;
        }
        if (type.equals("weaptrans")) {
            return weaptrans;
        }
        if (type.equals("eletrans")) {
            return eletrans;
        }
        if (type.equals("weapEletrans")) {
            return weapEletrans;
        }

        String[] origin = merge(nameList, typeList, weapList, eleList, weapEleList);
        String[] trans = merge(nametrans, typetrans, weaptrans, eletrans, weapEletrans);

        if (type.equals("trans")) {
            return trans;
        }
        if (type.equals("origin")) {
            return origin;
        }

        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < origin.length; j++) {
                if (original[i].equals(origin[j])) {
                    result[i] = trans[j];
                    break;
                }
            }
            if (result[i] == null) {
                for (int j = 0; j < trans.length; j++) {
                    if (original[i].equals(trans[j])) {
                        result[i] = origin[j];
                        break;
                    }
                }
            }
            if (result[i] == null) {
                result[i] = original[i];
            }
        }

        return result;
    }

    public boolean checkType(String typeName) {
        String[] na = {""};
        String[] typeList = htmlTrans(na, "typetrans");
        for (int i = 0; i < typeList.length; i++) {
            if (this.type[i] == 1 && typeName.equals(typeList[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean arrayContains(String[] arr, String tar) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(tar)) {
                return true;
            }
        }
        return false;
    }

    public static int[] search(Card[] cards, String[] tar) throws Exception {
        boolean bannerFound=false;
        boolean exitCode = false;
        String[] simpName = simpArray(allNames);
        String[] na = {""};
        target = tar;
        targetHtml = htmlTrans(target, "");
        int[] result = new int[Saoif.MAXCARDNUM];
        for (int i = 0; i < result.length; i++) {
            result[i] = 0;
        }
        String[] all = htmlTrans(na, "trans");
        System.out.printf("Searching");

        int number;
        

        for (int i = 0; i < cards.length; i++) {

            cards[i].match = true;

            for (int j = 0; j < targetHtml.length; j++) {
                if (i % 100 == 0) {
                    System.out.printf(".");
                }
                try {
                    number = Integer.parseInt(tar[j]);
                    if (number == cards[i].num) {
                        break;
                    } else {
                        cards[i].match = false;
                        break;
                    }
                } catch (NumberFormatException e) {
                }
                if (arrayContains(titleList, target[j])) {
                    if (cards[i].title.equals(target[j])) {
                        int[] answer = {cards[i].num};
                        System.out.println();
                        return answer;
                    } else {
                        cards[i].match = false;
                        break;
                    }
                }
                if (tar[j].equals(cards[i].name) || tar[j].equals(cards[i].weap) || tar[j].equals(cards[i].ele) || tar[j].equals(cards[i].weapEle)) {
                    continue;
                }
                if (target[j].equals("Ability")){
                    if (cards[i].weap.equals("Ability")){
                        continue;
                    }else{
                        cards[i].match = false;
                        break;
                    }
                }
                if (cards[i].checkType(tar[j])) {
                    continue;
                }
                if (arrayContains(all, tar[j])) {
                    cards[i].match = false;
                    break;
                }
                if (arrayContains(simpName,target[j])){
                    cards[i].match = false;
                    break;
                }
                if (buffList.contains(target[j])){
                    if (cards[i].buffs.contains(target[j])){
                        continue;
                    }else{
                        cards[i].match = false;
                        break;
                    }
                }
                
                try{
                    File file = new File("web".concat(File.separator).concat(String.valueOf(cards[i].num)).concat(".html"));
                    Scanner s = new Scanner(file);
                    String data = s.nextLine();
                    
                    if (targetHtml[j].contains("banners/")&&bannerFound==false&&data.contains(targetHtml[j])){
                        bannerFound = true;
                    }

                    if (!data.contains(targetHtml[j])) {
                        cards[i].match = false;
                        if (bannerFound){
                            exitCode = true;
                        }
                        break;
                    }
                }catch(Exception e){
                    cards[i].match = false;
                    break;
                }

            }
            if (exitCode){
                break;
            }

            if (cards[i].match) {
                result[realLenNums(result)] = cards[i].num;
            }
        }
        System.out.println();

        System.gc();

        return result;
    }

    public static void movePic(Card card) {
        String file = "wanted_cards".concat(File.separator).concat(String.valueOf(card.num)).concat(".jpg");
        FileDownloader.copyFile(card.pic.getPath(), file);
    }

    public static String[] findTitle(String file) {
        String regex = "\">([^<]*)\\s*<!-- -->\\s*【([^】]*)】";
        String[] result = new String[2];
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(file);

        if (matcher.find()) {
            result[0] = matcher.group(1).trim();
            result[1] = matcher.group(2).trim();
            return result;
        } else {
            return null; // No match found
        }
    }
    
    public static void addIfUnique(String[] array, String target){
        boolean need = true;
        for (int i=0; i<array.length; i++){
            if (target.equals(array[i])){
                need = false;
                break;
            }
        }
        if (need){
            array[realLen(array)] = target;
        }
    }
    
    public static String[] simpArray(String[] array){
        String[] result = new String[realLen(array)];
        for (int i=0; i<realLen(array);i++){
            result[i] = array[i];
        }
        return result;
    } 
    
    public static String getBuffs(String file){
        //〔攻擊強化4〕
        // Regular expression to find all occurrences inside 〔〕
        String regex = "〔(.*?)〕";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(file);
        
        // Use a LinkedHashSet to maintain insertion order and avoid duplicates
        Set<String> uniqueParts = new LinkedHashSet<>();

        // Find all matches
        while (matcher.find()) {
            uniqueParts.add(matcher.group(0)); // Add the whole match (including 〔 and 〕)
        }

        // Combine unique parts into a single string
        return String.join("", uniqueParts);
    }
    
    public static String[] getBanner(String filename) {
        List<String> matches = new ArrayList<>();
        StringBuilder content = new StringBuilder();
        
        // Read the file content
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use regex to find all occurrences of strings in "【】"
        Pattern pattern = Pattern.compile("【(.*?)】");
        Matcher matcher = pattern.matcher(content.toString());

        while (matcher.find()) {
            matches.add(matcher.group(1)); // Add the captured group (inside brackets)
        }

        // Convert List to String array
        return matches.toArray(new String[0]);
    }
}
