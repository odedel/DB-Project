package BL;

//import DAO;

public class User {

	private boolean isLogged = false;
	private String strUserName;
	private String strFavCountry = null;
	private int playerScore;
	
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
	
	public void setFavCountry(String strCountry)
	{
		this.strFavCountry = strCountry;
	}

	public String getFavCountry()
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
	
	public static boolean registerUser(String userName, String Password)
	{
		//add here saving player Score to DB
		createUser(userName);
		//Return success
		return (true);
	}
}
