package main;

import dao.DAO;
import utils.DBUser;

import java.util.LinkedList;
import java.util.List;

public class Check {

    public static void main(String[] args) throws Exception {
        DAO dao = new DAO();

        dao.connect(DBUser.MODIFIER);

        List<Integer> l = new LinkedList<>();
        l.add(1132);
        l.add(1258);
        l.add(1118);

        System.out.println(dao.getBirthPlace(95419));

        dao.disconnect();
    }
}
