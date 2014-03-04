/**
 * This is a module that extends the capabilities of the base module.
 * It adds the TextValueAndPolarityGenerator and WordValueAndPolarityGenerator PRs.
 * 
 * @author David Moreno Briz
 *
 */

package gateModules;

import java.net.URL;

import gate.Gate;
import processingResources.TextValueAndPolarityGenerator;
import webProcessingResources.WordValueAndPolarityGenerator;

public class DictionaryBasedSentimentAnalyzer extends DictionaryBasedInformationExtractor{
	
	/**
	 * Constructor of the module called DictionaryBasedSentimentAnalyzer based on DictionaryBasedInformationExtractor.
	 * It adds the TextValueAndPolarityGenerator and WordValueAndPolarityGenerator PRs.
	 * 
	 * @param name name of the module
	 * @param listsURL location of the lists to set the gazetteer. In URL format.
	 * @throws Exception
	 */
	public DictionaryBasedSentimentAnalyzer(String name, URL listsURL) throws Exception {
		super(name,listsURL);
		this.add(getCountTokens());
		this.add(getWordsSentimets());
		
	}
	
	/**
	 * Used only in the local mode.
	 * 
	 * Register our own plugins located in bin/processingResources/
	 * so we can use it in our controller.
	 *  
	 * @throws Exception
	 */
	public void registerPrPlugin() throws Exception{
		Gate.getCreoleRegister().registerDirectories(this.getClass().getResource("/processingResources/"));
		Gate.getCreoleRegister().registerDirectories(this.getClass().getResource("/webProcessingResources/"));
	}

	/**
	 * Get the configured TextValueAndPolarityGenerator PR, 
	 * which counts the number of times a sentiment word is said
	 * in a document according with our dictionaries and generates the document value.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public static TextValueAndPolarityGenerator getCountTokens() throws Exception{
		TextValueAndPolarityGenerator count = new TextValueAndPolarityGenerator(); //Create the PR
		count.setName("Count"); //Set its name
		return count;
	}
	
	/**
	 * Used for the web service, where a GET petition only have one document to analyze.
	 * 
	 * @return an array with the value and polarity of an analyzed document.
	 */
	public static String[] getAnalysisResult(){
		return processingResources.TextValueAndPolarityGenerator.getAnalysisResult();
	}
	
	/**
	 * Get the configured Count Sentiment For Each WOrd PR, 
	 * which analyze each word in a given document.
	 *
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public static WordValueAndPolarityGenerator getWordsSentimets() throws Exception{
		WordValueAndPolarityGenerator count = new WordValueAndPolarityGenerator(); //Create the PR
		count.setName("Count"); //Set its name
		return count;
	}
	
	/**
	 * Used for the web service, where a GET petition only have one document to analyze.
	 * 
	 * @return an array with each word in a given document and its associated values. See the PR for more infromation.
	 */
	public static String[][] getWordsAndValues(){
		return webProcessingResources.WordValueAndPolarityGenerator.getWordsAndValues();
	}
}