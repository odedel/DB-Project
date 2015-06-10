package main;

import dao.DAO;
import dao.DAOException;
import utils.DBUser;

public class Check {

    public static void main(String[] args) throws DAOException {
        DAO dao = new DAO();

        dao.connect(DBUser.PLAYER);
        for (String c : dao.getRandomCountries(10)) {
            System.out.println(c);
        }
    }
}
