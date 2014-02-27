package gateModules;

import pr.CountSentiment;
import pr.CountSentimentOfEachWord;

public class ModuleCount {
	
	/**
	 * Get the configured Count PR, 
	 * which counts the number of times a sentiment word is said
	 * in a document according with our dictionaries.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public static CountSentiment getCountTokens() throws Exception{
		CountSentiment count = new CountSentiment(); //Create the PR
		count.setName("Count"); //Set its name
		return count;
	}
	
	public static String[] resultadoAnalisis(){
		return pr.CountSentiment.resultadoAnalisis();
	}
	
	/**
	 * Get the configured Count Sentiment For Each WOrd PR, 
	 * which counts the number of times a sentiment word is said
	 * in a document according with our dictionaries.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public static CountSentimentOfEachWord getWordsSentimets() throws Exception{
		CountSentimentOfEachWord count = new CountSentimentOfEachWord(); //Create the PR
		count.setName("Count"); //Set its name
		return count;
	}
	
	public static String[][] getWordsAndValues(){
		return pr.CountSentimentOfEachWord.getWordsAndValues();
	}
}