package triviaDB;

import org.eclipse.swt.SWT;

import core.gameRunner;
import db.dao.DAOException;
import java.io.IOException;

public class TriviaMain {

	/**
	 * Execute to play trivia!
	 */
	public static void main(String[] args) 
	{
		GUITrivia gui = new GUITrivia();
		gui.open();
	
	}

}
