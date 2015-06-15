package BL;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import utils.DataNotFoundException;
import utils.EntityNotFound;
import utils.IDName;
import dao.*; 

public class QuestionFactory {

	final int NUMBER_OF_QUESTIONS_WITH_COUNTRY = 11;
	final int NUMBER_OF_QUESTIONS = 17;
	private DAO access;
	
	public QuestionFactory(DAO acc) {
		this.access = acc;
	}

	public Question createNewQuestion(IDName strFavCountry) throws DAOException, DataNotFoundException, EntityNotFound
	{
		int nRand;
		Random random = new Random();
		
		//this check if the current question is on specific country for the player
		if(strFavCountry == null)
		{
			// 
			nRand = random.nextInt(NUMBER_OF_QUESTIONS_WITH_COUNTRY - 1) + 1;
		}
		//
		else
		{
			strFavCountry = (IDName) this.access.getRandomCountries(1).toArray()[0];
			nRand = random.nextInt(NUMBER_OF_QUESTIONS - 1) + 1;
		}
		
		
		switch(nRand)
		{
			case(1):
			{
				return(popQues(strFavCountry));
			}
			case(2):
			{
				return(CreateDateQues(strFavCountry));
			}
			case(3):
			{
				return(CityInCountryQues(strFavCountry));
			}
			case(4):
			{
				return(CityNotInCountryQues(strFavCountry));
			}
			case(5):
			{
				return(OldestCityInCountryQues(strFavCountry));
			}
			case(6):
			{
				return(PersonBornInCountryQues(strFavCountry));
			}
			case(7):
			{
				return(PersonNotInCountryQues(strFavCountry));
			}
			case(8):
			{
				return(BornInSameCountryAsQues(strFavCountry));
			}
			case(9):
			{
				return(BornNotInSameCountryAsQues(strFavCountry));
			}
			case(10):
			{
				return(WhenBornQues(strFavCountry));
			}
			case(11):
			{
				return(WhereWasBornQues(strFavCountry));
			}
			case(12):
			{
				//To be Filled
				return null;
			}
			case(13):
			{
				return(MostpopQues());
			}
			case(14):
			{
				return(leastpopQues());
			}
			case(15):
			{
				return(MorepopQues());
			}
			case(16):
			{
				return(LesspopQues());
			}		
			case(17):
			{
				return(OldestCountryQues());
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
	
	public Question BornInSameCountryAsQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		ArrayList<IDName> person = (ArrayList<IDName>) this.access.getRandomPersonsBornInCountry(country.getId(), 2);
		ArrayList<IDName> personNot = (ArrayList<IDName>) this.access.getPersonsNotBornInSameCountry(((IDName) person.toArray()[0]).getId(), 3);
		
		IDName nAnswer = (IDName) person.toArray()[0];

		return(new Question("Which person was born in the same country as" +((IDName) person.toArray()[1]).getName() +"?", 
							nAnswer.getName().toString(), 
							((IDName)personNot.toArray()[0]).getName().toString(), 
							((IDName)personNot.toArray()[1]).getName().toString(), 
							((IDName)personNot.toArray()[2]).getName().toString()));
	}
	
	public Question BornNotInSameCountryAsQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		ArrayList<IDName> person = (ArrayList<IDName>) this.access.getRandomPersonsBornInCountry(country.getId(), 4);
		ArrayList<IDName> personNot = (ArrayList<IDName>) this.access.getPersonsNotBornInSameCountry(((IDName) person.toArray()[0]).getId(), 1);
		
		IDName nAnswer = (IDName) personNot.toArray()[0];

		return(new Question("Which person was not born in the same country as " +((IDName) person.toArray()[1]).getName() +"?", 
							nAnswer.getName().toString(), 
							((IDName)person.toArray()[1]).getName().toString(), 
							((IDName)person.toArray()[2]).getName().toString(), 
							((IDName)person.toArray()[3]).getName().toString()));
	}
	
	public Question WhenBornQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		Date answer1, answer2, answer3;
		ArrayList<IDName> person = (ArrayList<IDName>) this.access.getRandomPersonsBornInCountry(country.getId(), 4);
		
		Date nAnswer = this.access.getPersonBirthDate(((IDName) person.toArray()[0]).getId());
		answer1 = this.access.getPersonBirthDate(((IDName) person.toArray()[1]).getId());
		answer2 = this.access.getPersonBirthDate(((IDName) person.toArray()[2]).getId());
		answer3 = this.access.getPersonBirthDate(((IDName) person.toArray()[3]).getId());

		return(new Question("When was born " +((IDName) person.toArray()[0]).getName() +"?", 
							nAnswer.toString(), 
							answer1.toString(), 
							answer2.toString(), 
							answer3.toString()));
	}
	
	/*
	public Question BornLastQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		Date answer1, answer2, answer3;
		ArrayList<IDName> person = (ArrayList<IDName>) this.access.getRandomPersonsBornInCountry(country.getId(), 4);
		
		Date nAnswer = this.access.getPersonBirthDate(((IDName) person.toArray()[0]).getId());
		answer1 = this.access.getPersonBirthDate(((IDName) person.toArray()[1]).getId());
		answer2 = this.access.getPersonBirthDate(((IDName) person.toArray()[2]).getId());
		answer3 = this.access.getPersonBirthDate(((IDName) person.toArray()[3]).getId());

		return(new Question("Which person was not born in the same country as " +((IDName) person.toArray()[0]).getName() +"?", 
							nAnswer.toString(), 
							answer1.toString(), 
							answer2.toString(), 
							answer3.toString()));
	}
	
	public Question BornFirstQues(IDName country) throws DAOException, DataNotFoundException, EntityNotFound
	{
		Date answer1, answer2, answer3;
		ArrayList<IDName> person = (ArrayList<IDName>) this.access.getRandomPersonsBornInCountry(country.getId(), 4);
		
		Date nAnswer = this.access.getPersonBirthDate(((IDName) person.toArray()[0]).getId());
		answer1 = this.access.getPersonBirthDate(((IDName) person.toArray()[1]).getId());
		answer2 = this.access.getPersonBirthDate(((IDName) person.toArray()[2]).getId());
		answer3 = this.access.getPersonBirthDate(((IDName) person.toArray()[3]).getId());

		return(new Question("Which person was not born in the same country as " +((IDName) person.toArray()[0]).getName() +"?", 
							nAnswer.toString(), 
							answer1.toString(), 
							answer2.toString(), 
							answer3.toString()));
	}*/
	
	//All Questions withOUT option to FAVORITE COUNTRY
	
	public Question MostpopQues() throws DAOException, DataNotFoundException, EntityNotFound
	{
		String[] answers = new String[3];
		String answerRight = "";
		int k = 0;
		ArrayList<IDName> countries = (ArrayList<IDName>) this.access.getRandomCountries(4);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		//Convert to ids
		for (int i = 0; i < 4; i++)
		{
			ids.add(((IDName)countries.toArray()[i]).getId());
		}
		
		int nAnswer = this.access.getMostPopulatedCountry(ids);
		
		//Convert to answers string
		for(int j = 0; j < 4; j++)
		{
			if(((IDName)countries.toArray()[0]).getId() == nAnswer)
			{
				answerRight = ((IDName)countries.toArray()[k]).getName();
			}
			else
			{
				answers[k] = ((IDName)countries.toArray()[k]).getName();
			}
		}
		
		return(new Question("Which country is the most populated", answerRight, answers[0], answers[1], answers[2]));
	}
	
	public Question leastpopQues() throws DAOException, DataNotFoundException, EntityNotFound
	{
		String[] answers = new String[3];
		String answerRight = "";
		int k = 0;
		ArrayList<IDName> countries = (ArrayList<IDName>) this.access.getRandomCountries(4);
		//
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		for (int i = 0; i < 4; i++)
		{
			ids.add(((IDName)countries.toArray()[i]).getId());
		}
		
		int nAnswer = this.access.getLeastPopulatedCountry(ids);
		
		for(int j = 0; j < 4; j++)
		{
			if(((IDName)countries.toArray()[0]).getId() == nAnswer)
			{
				answerRight = ((IDName)countries.toArray()[k]).getName();
			}
			else
			{
				answers[k] = ((IDName)countries.toArray()[k]).getName();
			}
		}
		
		return(new Question("Which country is the least populated", answerRight, answers[0], answers[1], answers[2]));
	}
	
	public Question MorepopQues() throws DAOException, DataNotFoundException, EntityNotFound
	{

		ArrayList<IDName> country = (ArrayList<IDName>) this.access.getRandomCountries(1);
		ArrayList<IDName> countries = (ArrayList<IDName>) this.access.getCountryThatIsMorePopulatedThan(((IDName)country.toArray()[0]).getId(),1);
		ArrayList<IDName> countriesNot = (ArrayList<IDName>) this.access.getCountryThatIsLessPopulatedThan(((IDName)country.toArray()[0]).getId(),3);
		
		return(new Question("Which country is more populated than"+ ((IDName)country.toArray()[0]).getName(), 
				((IDName)countries.toArray()[0]).getName(),
				((IDName)countriesNot.toArray()[0]).getName(),
				((IDName)countriesNot.toArray()[1]).getName(), 
				((IDName)countriesNot.toArray()[2]).getName()));
	}
	
	public Question LesspopQues() throws DAOException, DataNotFoundException, EntityNotFound
	{
		ArrayList<IDName> country = (ArrayList<IDName>) this.access.getRandomCountries(1);
		ArrayList<IDName> countries = (ArrayList<IDName>) this.access.getCountryThatIsLessPopulatedThan(((IDName)country.toArray()[0]).getId(),1);
		ArrayList<IDName> countriesNot = (ArrayList<IDName>) this.access.getCountryThatIsMorePopulatedThan(((IDName)country.toArray()[0]).getId(),3);
		
		return(new Question("Which country is Less populated than"+ ((IDName)country.toArray()[0]).getName(), 
				((IDName)countries.toArray()[0]).getName(),
				((IDName)countriesNot.toArray()[0]).getName(),
				((IDName)countriesNot.toArray()[1]).getName(), 
				((IDName)countriesNot.toArray()[2]).getName()));
	}
	
	public Question OldestCountryQues() throws DAOException, DataNotFoundException, EntityNotFound
	{
		String[] answers = new String[3];
		String answerRight = "";
		int k = 0;
		ArrayList<IDName> countries = (ArrayList<IDName>) this.access.getRandomCountries(4);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		for (int i = 0; i < 4; i++)
		{
			ids.add(((IDName)countries.toArray()[i]).getId());
		}
		
		int nAnswer = this.access.getOldestCountry(ids);
		
		for(int j = 0; j < 4; j++)
		{
			if(((IDName)countries.toArray()[0]).getId() == nAnswer)
			{
				answerRight = ((IDName)countries.toArray()[k]).getName();
			}
			else
			{
				answers[k] = ((IDName)countries.toArray()[k]).getName();
			}
		}
		
		return(new Question("Which country is the oldest?", answerRight, answers[0], answers[1], answers[2]));
	}
	
	/*
	public Question CreateBeforeAndAfterCountryQues() throws DAOException, DataNotFoundException, EntityNotFound
	{
		ArrayList<IDName> Beforecountry = (ArrayList<IDName>) this.access.getRandomCountries(1);
		ArrayList<IDName> Aftercountry = (ArrayList<IDName>) this.access.getRandomCountries(1);
		ArrayList<IDName> countries =  new ArrayList<IDName>();
		
		for (int i = 0; i < 4; i++) {
			countries.add((IDName) this.access.getCountryCreatedBetween(((IDName)Beforecountry.toArray()[0]).getId(),((IDName)Aftercountry.toArray()[0]).getId()));
		}
		
		return(new Question(" Which country Created before " + ((IDName)Beforecountry.toArray()[0]).getName() + " but after " +((IDName)Aftercountry.toArray()[0]).getName()
						, answerRight, answers[0], answers[1], answers[2]));
	}*/
}
