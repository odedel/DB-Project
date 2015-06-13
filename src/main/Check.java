package main;

import dao.DAO;
import dao.DAOException;
import utils.DBUser;
import utils.IDName;
import utils.IntegrityException;

import java.util.Collection;

public class Check {

    public static void main(String[] args) throws DAOException, IntegrityException {
        DAO dao = new DAO();

        dao.connect(DBUser.MODIFIER);

        System.out.println(dao.getNumberOfPeopleInCountry(2692));

        dao.disconnect();
    }
}
