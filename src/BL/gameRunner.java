package BL;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import collect_data.DataCollector;
import utils.DBUser;
import utils.DataNotFoundException;
import utils.EntityNotFound;
import utils.IDName;
import dao.DAO;
import dao.DAOException;
import main.*;
import utils.*;

public class gameRunner {
	  
	public static void main(String[] args) throws Exception {
		gameRunner gm = new gameRunner();
		gm.getCurrentQuestion();
	  }
	private User playerOne = null;
	private User playerTwo = null;
	private Question currQuestion = null;
	private QuestionFactory qFactory;
	private User currentPlayer;
	private int nPlayerOneWorngAnswers;
	private int nPlayerTwoWorngAnswers;
	private final int NUMBER_OF_WORNG_ALLOWED = 2;
	private DAO access;
	private Collection<IDName> allCountries;
	
	public gameRunner() throws DAOException {
		
		//CreateFactoryHere
		this.nPlayerOneWorngAnswers = 0;
		this.nPlayerTwoWorngAnswers = 0;
		this.access = new DAO();
		this.access.connect(DBUser.MODIFIER);
		this.qFactory = new QuestionFactory(this.access);
		this.allCountries = this.access.getAllCountries();
	}
	
	//Use this function when exit
	public void  CloseGameRunner()
	{
		this.access.disconnect();
	}
	
	//Return all countries, every node is ID and Name
	public Collection<IDName> getAllCountries() throws DAOException
	{
		return(this.allCountries);
	}
	
	//This function create Question if needed and return question string
	public String getCurrentQuestion() throws DAOException, DataNotFoundException, EntityNotFound
	{
		if(this.currQuestion == null)
		{
			this. currQuestion = this.qFactory.createNewQuestion(null);
		}
		
		return this.currQuestion.getQuestion();
	}

	//This function return shuffle answers
	public ArrayList<String> getAnswers()
	{
		return(this.currQuestion.getAnswers());
	}
	
	//Return false if user login failed or two player ar already logged
	public boolean logInUser(String strUserName, String strPassword) throws DAOException, DataNotFoundException, EntityNotFound
	{
		if(this.playerOne == null)
		{
			this.playerOne = new User(strUserName, strPassword, this.access);
			return(this.playerOne.isLoggedIn());
		}
		else if(this.playerTwo == null)
		{
			this.playerTwo = new User(strUserName, strPassword, this.access);
			return(this.playerTwo.isLoggedIn());
		}
		
		return false;
	}
	
	//Return false if user Register failed or Not
	public boolean registerUser(String strUserName, String strPassword) throws DAOException
	{
		return(User.registerUser(strUserName, strPassword,this.access) != 0);
	}
	
	//This function continue the game (switch player if need and get new Ques)
	// If game over throw exception
	public void nextPlay() throws Exception
	{
		if(!this.isCurrentGameFinished())
		{
			if(this.playerOne == null)
			{
				throw new Error("No userLoggedIn");
			}
			else if(this.playerTwo != null)
			{
				this.SwitchPlayer();
			}
			
			this.currQuestion = this.qFactory.createNewQuestion(this.currentPlayer.getFavCountry());
		}
		else
		{
			throw new Exception("Game Is Over, Init game to play again");
		}
	}
	
	private void SwitchPlayer()
	{
		if(this.currentPlayer == this.playerOne)
		{
			this.currentPlayer = this.playerTwo;
		}
		else
		{
			this.currentPlayer = this.playerOne;
		}
	}
	
	// This function check if player right, add score and continue to next Q
	public boolean checkPlayerAnswer(String strAns) throws Exception
	{
		Boolean isRight = false;
		
		if(this.currQuestion.checkAnswer(strAns))
		{
			this.currentPlayer.addRightAnswerScore();
			this.currentPlayer.setUserAnsweredCorrectly();
			isRight = true;
		}
		else
		{
			this.addWrongAnswer();
		}
		
		this.nextPlay();
		return(isRight);
	}
	
	//Add wrong answer to current user
	private void addWrongAnswer() throws DAOException, EntityNotFound
	{
		if(this.currentPlayer == this.playerOne)
		{
			this.nPlayerOneWorngAnswers++;
		}
		else
		{
			this.nPlayerTwoWorngAnswers++;
		}
		
		this.currentPlayer.setUserAnsweredWrong();
	}
	
	//check if game finished
	private boolean isCurrentGameFinished()
	{
		if((this.nPlayerOneWorngAnswers > NUMBER_OF_WORNG_ALLOWED) || (this.nPlayerTwoWorngAnswers > NUMBER_OF_WORNG_ALLOWED))
		{
			return (true);
		}
		
		return false;
	}
	
	//This function init game to non wrong answers and player one to start
	public void StartGame() throws DAOException, EntityNotFound
	{
		this.nPlayerOneWorngAnswers = 0;
		this.nPlayerTwoWorngAnswers = 0;
		
		if(this.playerOne != null)
		{
			this.playerOne.initPlayerScore();
			this.currentPlayer = this.playerOne;
			this.playerOne.setUserStartedNewGame();
		}
		
		if(this.playerTwo != null)
		{
			this.playerTwo.initPlayerScore();
			this.playerTwo.setUserStartedNewGame();
		}
	}
	
	//Update DataFrom Yago
	public void updateDataFromSource() throws DAOException, IOException
	{
		this.access.deleteDB();
		DataCollector dataCollector = new DataCollector();
		dataCollector.collectData();
		this.access.uploadDataCollector(dataCollector);
	}
	
	//Count how many players connected 
	public int getHowPlayerConnected()
	{
		int nCount = 0;
		
		if(this.playerOne != null)
		{
			nCount++;
		}
		
		if(this.playerTwo != null)
		{
			nCount++;
		}
		
		return (nCount);
	}
	
	//this function return who has  
	public int currentWinner()
	{
		int nCount = 0;
		
		if(this.playerOne != null)
		{
			nCount++;
		}
		
		if(this.playerTwo != null)
		{
			nCount++;
		}
		
		return (nCount);
	}
	
	public Collection<UserIDScoreDate> getBestScores() throws DAOException
	{
		return(this.access.getTopScore(10));
	}
}
