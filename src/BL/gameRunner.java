package BL;

import java.util.ArrayList;
import java.util.Collection;

import utils.IDName;
import dao.DAO;
import dao.DAOException;

public class gameRunner {
	  
	public static void main(String[] args) throws Exception {
		new gameRunner();
	  }
	private User playerOne = null;
	private User playerTwo = null;
	private Question currQuestion;
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
		this.qFactory = new QuestionFactory();
		this.access = new DAO();
		this.allCountries = this.access.getAllCountries();
	}
	
	public Collection<IDName> getAllCounter() throws DAOException
	{
		return(this.allCountries);
	}
	
	public String getCurrentQuestion()
	{
		return this.currQuestion.getQuestion();
	}

	public ArrayList<String> getAnswers()
	{
		return(this.currQuestion.getAnswers());
	}
	
	//Return false if user login failed or two player ar already logged
	public boolean logInUser(String strUserName, String strPassword)
	{
		if(this.playerOne == null)
		{
			this.playerOne = new User(strUserName, strPassword);
			return(this.playerOne.isLoggedIn());
		}
		else if(this.playerTwo == null)
		{
			this.playerTwo = new User(strUserName, strPassword);
			return(this.playerTwo.isLoggedIn());
		}
		
		return false;
	}
	
	//Return false if user Register failed or Not
	public boolean registerUser(String strUserName, String strPassword) throws DAOException
	{
		return(User.registerUser(strUserName, strPassword));
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
	private void addWrongAnswer()
	{
		if(this.currentPlayer == this.playerOne)
		{
			this.nPlayerOneWorngAnswers++;
		}
		else
		{
			this.nPlayerTwoWorngAnswers++;
		}
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
	
	//This function init game to non wrong answers
	public void initGame()
	{
		this.nPlayerOneWorngAnswers = 0;
		this.nPlayerTwoWorngAnswers = 0;
	}
}
