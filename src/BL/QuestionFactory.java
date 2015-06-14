package BL;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import utils.DataNotFoundException;
import utils.EntityNotFound;
import utils.IDName;
import dao.*; 

public class QuestionFactory {

	final int NUMBER_OF_QUESTIONS = 10;
	private DAO access;
	
	public QuestionFactory() {
		this.access = new DAO();
	}

	public Question createNewQuestion(IDName strFavCountry) throws DAOException, DataNotFoundException, EntityNotFound
	{
		int nRand;
		
		//
		if(strFavCountry == null)
		{
			// 
			nRand = 1;
		}
		//
		else
		{
			strFavCountry = (IDName) this.access.getRandomCountries(1).toArray()[0];
			nRand = 2;
		}
		
		
		switch(nRand)
		{
			case(1):
			{
				return(popQues(strFavCountry));
			}
		}
		
		return(null);
	}
	
	//First Are All Questions with option to FAVORITE COUNTRY
	
	public Question popQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		int answer1, answer2, answer3;
		ArrayList<IDName> countries = (ArrayList<IDName>) this.access.getRandomCountries(3);
		int nAnswer = this.access.getNumberOfPeopleInCountry(country.getId());
		answer1 = this.access.getNumberOfPeopleInCountry(((IDName)countries.toArray()[0]).getId());
		answer2 = this.access.getNumberOfPeopleInCountry(((IDName)countries.toArray()[1]).getId());
		answer3 = this.access.getNumberOfPeopleInCountry(((IDName)countries.toArray()[2]).getId());
		
		return(new Question("How many people lives in" + country.getName(), 
							Integer.toString(nAnswer), Integer.toString(answer1), Integer.toString(answer2), Integer.toString(answer3)));
	}
	
	public Question CreateDateQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		Date answer1, answer2, answer3;
		ArrayList<IDName> countries = (ArrayList<IDName>) this.access.getRandomCountries(3);
		Date nAnswer = this.access.getCreationDate(country.getId());
		answer1 = this.access.getCreationDate(((IDName)countries.toArray()[0]).getId());
		answer2 = this.access.getCreationDate(((IDName)countries.toArray()[1]).getId());
		answer3 = this.access.getCreationDate(((IDName)countries.toArray()[2]).getId());
		
		return(new Question(" When does "+ country.getName() +" created" + country.getName(), 
							nAnswer.toString(), answer1.toString(), answer2.toString(), answer3.toString()));
	}
	
	public Question CityInCountryQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		IDName answer1, answer2, answer3;
		ArrayList<IDName> cities = (ArrayList<IDName>) this.access.getRandomCitiesByCountry(country.getId(), 3);
		IDName nAnswer = (IDName) this.access.getRansomCitiesNotInCountry(country.getId(), 1).toArray()[0];
		answer1 = (IDName) cities.toArray()[0];
		answer2 = (IDName) cities.toArray()[1];
		answer3 = (IDName) cities.toArray()[2];
		
		return(new Question("Which city is in " + country.getName(), 
							nAnswer.getName().toString(), answer1.getName().toString(), answer2.getName().toString(), answer3.getName().toString()));
	}
	
	public Question CityNotInCountryQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		IDName answer1, answer2, answer3;
		ArrayList<IDName> cities = (ArrayList<IDName>) this.access.getRansomCitiesNotInCountry(country.getId(), 3);
		IDName nAnswer = (IDName) this.access.getRandomCitiesByCountry(country.getId(), 1).toArray()[0];
		answer1 = (IDName) cities.toArray()[0];
		answer2 = (IDName) cities.toArray()[1];
		answer3 = (IDName) cities.toArray()[2];
		
		return(new Question("Which city is not in" + country.getName(), 
							nAnswer.getName().toString(), answer1.getName().toString(), answer2.getName().toString(), answer3.getName().toString()));
	}
	
	public Question OldestCityInCountryQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		IDName answer1, answer2, answer3;
		ArrayList<IDName> cities = (ArrayList<IDName>) this.access.getRandomCitiesByCountry(country.getId(), 3);
		IDName nAnswer = (IDName) this.access.getOldestCity(country.getId());
		answer1 = (IDName) cities.toArray()[0];
		answer2 = (IDName) cities.toArray()[1];
		answer3 = (IDName) cities.toArray()[2];
		
		return(new Question("What is the oldest city in" + country.getName(), 
							nAnswer.getName().toString(), answer1.getName().toString(), answer2.getName().toString(), answer3.getName().toString()));
	}
	
	public Question PersonBornInCountryQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		IDName answer1, answer2, answer3;
		ArrayList<IDName> persons = (ArrayList<IDName>) this.access.getRandomPersonsBornInCountry(country.getId(), 3);
		IDName nAnswer = (IDName) this.access.getRandomPersonsNotBornInCountry(country.getId(),1).toArray()[0];
		answer1 = (IDName) persons.toArray()[0];
		answer2 = (IDName) persons.toArray()[1];
		answer3 = (IDName) persons.toArray()[2];
		
		return(new Question("Which person born in" + country.getName(), 
							nAnswer.getName().toString(), answer1.getName().toString(), answer2.getName().toString(), answer3.getName().toString()));
	}
	
	public Question PersonNotInCountryQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		IDName answer1, answer2, answer3;
		ArrayList<IDName> persons = (ArrayList<IDName>) this.access.getRandomPersonsBornInCountry(country.getId(), 3);
		IDName nAnswer = (IDName) this.access.getRandomPersonsNotBornInCountry(country.getId(), 1).toArray()[0];
		answer1 = (IDName) persons.toArray()[0];
		answer2 = (IDName) persons.toArray()[1];
		answer3 = (IDName) persons.toArray()[2];
		
		return(new Question("Which person lives in other country than the other three", 
							nAnswer.getName().toString(), answer1.getName().toString(), answer2.getName().toString(), answer3.getName().toString()));
	}
	
	public Question WhereWasBornQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		IDName answer1, answer2, answer3;
		ArrayList<IDName> persons = (ArrayList<IDName>) this.access.getRandomPersonsBornInCountry(country.getId(), 4);
		IDName nAnswer = this.access.getBirthCity(((IDName) persons.toArray()[0]).getId());
		answer1 = this.access.getBirthCity(((IDName) persons.toArray()[1]).getId());
		answer2 = this.access.getBirthCity(((IDName) persons.toArray()[2]).getId());
		answer3 = this.access.getBirthCity(((IDName) persons.toArray()[3]).getId());
		
		return(new Question("Where does " + ((IDName)persons.toArray()[0]).getName() +" born?", 
							nAnswer.getName().toString(), answer1.getName().toString(), answer2.getName().toString(), answer3.getName().toString()));
	}
	
	//All Questions withOUT option to FAVORITE COUNTRY
	
/*	public Question MostpopQues()
	{
		String quesStr;
		String answer1, answer2, answer3, rightAns;
		ArrayList<IDName> countries = (ArrayList<IDName>) this.access.getRandomCountries(4);
		//
		int nAnswer = this.access.getMostPopulatedCountry(ls);
		
		return(new Question("Which country is the most populated", "", "", "", ""));
	}
	*/
/*	public Question leastpopQues()
	{
		String quesStr;
		String answer1, answer2, answer3, rightAns;
		ArrayList<String> countries = getRandomCountries(4);
		int nAnswer = getLeastPopulatedCountry(countries);
		
		return(new Question("Which country is the least populated ", "", "", "", ""));
	}*/
	
}
