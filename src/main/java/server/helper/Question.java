/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author admin
 */
public class Question {
    public static String renQuestion() {
        List<Integer> listAnswer = new ArrayList<>();
        
        int a = randomInt100();
        int b = randomInt100();
//        System.out.println("a: " + a);
//        System.out.println("b: " + b);
        int result = a + b;
//        System.out.println("result: " + result);
        int answer1 = result + randomInt10();
        int answer2 = result - randomInt10();
        int answer3 = result + 10;
        
//        System.out.println("aanswer 1: " + answer1);
//        System.out.println("aanswer 2: " + answer2);
//        System.out.println("aanswer 3: " + answer3);
        
        listAnswer.add(result);
        listAnswer.add(answer1);
        listAnswer.add(answer2);
        listAnswer.add(answer3);
//        System.out.println("--------------------------");
        Collections.shuffle(listAnswer);
        int i = 1;
        String msg = "";
        msg = msg + a + ";" + b + ";";
        
        for(Integer x: listAnswer) {
//            System.out.println("dap an " + i + " : " + x);
            i++;
            msg += x + ";";
        }
        
//        System.out.println(msg);
        return msg;
    }
    
    static int randomInt100 () {
        double random = Math.random();
        random = random * 100 + 1;
        int randomInt = (int) random;
//        System.out.println("Random number is : " + randomInt);
        return randomInt;
    }
    
    static int randomInt10 () {
        int randomInt = (int)(Math.random() * 15 * (Math.random() > 0.5 ? 1 : -1));
        randomInt += 5;
//        System.out.println("Random number is : " + randomInt);
        if (randomInt == 0) {
            return 1;
        }
        return randomInt;
    }
    
//    public static void main(String[] args) {
//        renQuestion();
//    }
}
