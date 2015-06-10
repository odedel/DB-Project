package main;

import dao.DAO;
import dao.DAOException;
import utils.DBUser;

public class Check {

    public static void main(String[] args) throws DAOException {
        DAO dao = new DAO();

        dao.connect(DBUser.MODIFIER);
        for (String c : dao.getRandomCitiesByCountry("Israel", 10)) {
            System.out.println(c);
        }

        System.out.println(dao.createUser("Oded2231231232"));

        dao.disconnect();
    }
}
