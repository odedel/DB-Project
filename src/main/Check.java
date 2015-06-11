package main;

import dao.DAO;
import dao.DAOException;
import utils.DBUser;
import utils.IntegrityException;

import java.util.Collection;

public class Check {

    public static void main(String[] args) throws DAOException, IntegrityException {
        DAO dao = new DAO();

        dao.connect(DBUser.MODIFIER);
//        for (String c : dao.getRandomCitiesIDsByCountry("Israel", 10)) {
//            System.out.println(c);
//        }

//        System.out.println(dao.createUser("Oded2231231232"));

        Collection<Integer> cities_ids = dao.getRandomCitiesByCountry(dao.getID("Country", "Israel"), 5);

        for (Integer id : cities_ids) {
            System.out.println(id);
        }

        dao.disconnect();
    }
}
