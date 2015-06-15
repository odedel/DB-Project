package main;

import dao.DAO;
import utils.DBUser;

import java.util.LinkedList;
import java.util.List;

public class Check {

    public static void main(String[] args) throws Exception {
        DAO dao = new DAO();

        dao.connect(DBUser.MODIFIER);

        System.out.println(dao.checkPassword(1, "Ode12312d"));

        dao.disconnect();
    }
}
