package main;

import dao.DAO;
import utils.DBUser;

public class Check {

    public static void main(String[] args) throws Exception {
        DAO dao = new DAO();

        dao.connect(DBUser.MODIFIER);

        System.out.println(dao.getUserName(1222));

        dao.disconnect();
    }
}
