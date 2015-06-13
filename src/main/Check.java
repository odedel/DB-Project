package main;

import dao.DAO;
import dao.DAOException;
import utils.DBUser;
import utils.DataNotFoundException;

public class Check {

    public static void main(String[] args) throws DAOException, DataNotFoundException {
        DAO dao = new DAO();

        dao.connect(DBUser.MODIFIER);

        System.out.println(dao.getUserName(2692));

        dao.disconnect();
    }
}
