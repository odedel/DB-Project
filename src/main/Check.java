package main;

import dao.DAO;
import utils.DBUser;
import utils.UserIDScoreDate;

import java.util.Date;

public class Check {

    public static void main(String[] args) throws Exception {
        DAO dao = new DAO();

        dao.connect(DBUser.MODIFIER);

        System.out.println(dao.getCountryNotCreatedBetween(363, 364));

        dao.disconnect();
    }
}
