package main;

import db.DBConnection;

/**
 * Created by Oded on 08/05/2015.
 */
public class Main {

    public static void main(String[] args) {
//        Question q = new Question("Hello?", "Hey", "a", "b", "c");
//
//        System.out.println(q.checkAnswer("Hey"));
//        System.out.println(q.checkAnswer("a"));
//
//        System.out.println(QuestionGenerator.questionGenerator());

        DBConnection connector = new DBConnection();
        connector.connect();
        connector.close();
    }
}