/**
 * This is a module that extends the capabilities of the base module.
 * It adds the CountSentiment and CountSentimetsOfEachWord PRs.
 * 
 * @author David Moreno Briz
 *
 */

package gateModules;

import gate.Gate;
import pr.CountSentiment;
import pr.CountSentimentOfEachWord;

public class ModuleCount extends ModuleWD{
	
	/**
	 * Constructor of the module called ModuleCount based on ModuleWD.
	 * It adds the CountSentiment and CountSentimentOfEachWord PRs.
	 * 
	 * @param name name of the module
	 * @throws Exception
	 */
	public ModuleCount(String name) throws Exception {
		super(name);
		this.add(getCountTokens());
		this.add(getWordsSentimets());
		
	}
	
	/**
	 * Used only in the local mode.
	 * 
	 * Register our own plugins located in bin/pr/
	 * so we can use it in our controller.
	 *  
	 * @throws Exception
	 */
	public void registerPrPlugin() throws Exception{
		Gate.getCreoleRegister().registerDirectories(this.getClass().getResource("/pr/"));
	}

	/**
	 * Get the configured CountSentiment PR, 
	 * which counts the number of times a sentiment word is said
	 * in a document according with our dictionaries and generates the document value.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public static CountSentiment getCountTokens() throws Exception{
		CountSentiment count = new CountSentiment(); //Create the PR
		count.setName("Count"); //Set its name
		return count;
	}
	
	/**
	 * Used for the web service, where a GET petition only have one document to analyze.
	 * 
	 * @return an array with the value and polarity of an analyzed document.
	 */
	public static String[] getAnalysisResult(){
		return pr.CountSentiment.getAnalysisResult();
	}
	
	/**
	 * Get the configured Count Sentiment For Each WOrd PR, 
	 * which analyze each word in a given document.
	 *
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public static CountSentimentOfEachWord getWordsSentimets() throws Exception{
		CountSentimentOfEachWord count = new CountSentimentOfEachWord(); //Create the PR
		count.setName("Count"); //Set its name
		return count;
	}
	
	/**
	 * Used for the web service, where a GET petition only have one document to analyze.
	 * 
	 * @return an array with each word in a given document and its associated values. See the PR for more infromation.
	 */
	public static String[][] getWordsAndValues(){
		return pr.CountSentimentOfEachWord.getWordsAndValues();
	}
}