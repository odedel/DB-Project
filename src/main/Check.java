package main;

import dao.DAO;
import dao.DAOException;
import utils.DBUser;
import utils.IntegrityException;

public class Check {

    public static void main(String[] args) throws DAOException, IntegrityException {
        DAO dao = new DAO();

        dao.connect(DBUser.MODIFIER);
        for (String c : dao.getRandomCitiesByCountry("Israel", 10)) {
            System.out.println(c);
        }

//        System.out.println(dao.createUser("Oded2231231232"));

        System.out.println(dao.getUserID("Oded"));
        dao.setUserAnsweredCorrectly(1);
        dao.setUserAnsweredWrong(1);
        dao.setUserStartedNewGame(1);

        dao.disconnect();
    }
}
