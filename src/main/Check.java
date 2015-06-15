package main;

import dao.DAO;
import utils.DBUser;
import utils.UserIDScoreDate;

import java.util.Date;

public class Check {

    public static void main(String[] args) throws Exception {
        DAO dao = new DAO();

        dao.connect(DBUser.MODIFIER);

        for (UserIDScoreDate x : dao.getTopScoreByUser(1, 100000)) {
            System.out.println(x.getUserID() + " " + x.getScore() + " " + x.getDate());
        }

        dao.disconnect();
    }
}
