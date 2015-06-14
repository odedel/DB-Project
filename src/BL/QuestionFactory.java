package BL;

import java.util.ArrayList;

public class QuestionFactory {

	final int NUMBER_OF_QUESTIONS = 10;
	
	public QuestionFactory() {
		// TODO Auto-generated constructor stub
	}

	private Question createNewQuestion()
	{
		String strRand = "";
		
		//ArrayList<String> countries = getRandomCountries(1);
		//strRand = countries[0];

		return(createNewQuestion(strRand));
	}
	
	public Question createNewQuestion(String strFavCountry)
	{
		int nRand;
		
		//
		if(strFavCountry != "")
		{
			// 
			nRand = 1;
		}
		//
		else
		{
			strFavCountry = "random Contry";
			nRand = 2;
		}
		
		
		switch(nRand)
		{
			case(1):
			{
			//	arra
			//	quesStr = "How many people lives in" + 
			//	break;
			}
		}
		

		
		return(new Question("", "", "", "", ""));
	}
	
	public Question popQues(String country)
	{
		String quesStr;
		String answer1, answer2, answer3, rightAns;
		ArrayList<String> countries = getRandomCountries(3);
		int nAnswer = getNumberOfPeopleInCountry(country)
		return(new Question("How many people lives in", "", "", "", ""));
	}
	
	public Question MostpopQues()
	{
		String quesStr;
		String answer1, answer2, answer3, rightAns;
		ArrayList<String> countries = getRandomCountries(4);
		int nAnswer = getMostPopulatedCountry(countries);
		
		return(new Question("Which country is the most populated", "", "", "", ""));
	}
	
	public Question leastpopQues()
	{
		String quesStr;
		String answer1, answer2, answer3, rightAns;
		ArrayList<String> countries = getRandomCountries(4);
		int nAnswer = getLeastPopulatedCountry(countries);
		
		return(new Question("Which country is the least populated ", "", "", "", ""));
	}
	
}
