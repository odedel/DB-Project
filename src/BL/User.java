package BL;

import utils.IDName;
import dao.*;

public class User {

	private boolean isLogged = false;
	private String strUserName;
	private IDName strFavCountry = null;
	private int playerScore;
	private static DAO access = new DAO();
	
	public User(String userName, String password) {
		this.strUserName = userName;
		this.isLogged = this.login(password); 
	}
	
	public boolean login(String pass)
	{
		//Call them log in
		//this.isLogged =
		return (true);
	}
	
	public boolean isLoggedIn()
	{
		return this.isLogged;
	}
	
	public void setFavCountry(IDName strCountry)
	{
		this.strFavCountry = strCountry;
	}

	public IDName getFavCountry()
	{
		return (this.strFavCountry);
	}
	
	public int getUserScore()
	{
		return (this.playerScore);
	}
	
	public void addRightAnswerScore()
	{
		this.playerScore++;
	}
	
	public boolean savePlayerScore()
	{
		//add here saving player Score to DB
		
		//Return success
		return (true);
	}
	
	public static boolean registerUser(String userName, String Password) throws DAOException
	{
		//add here saving player Score to DB
		access.createUser(userName);
		
		//Return success
		return (true);
	}
}
