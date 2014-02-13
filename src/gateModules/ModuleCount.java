package gateModules;

import pr.CountSentiment;

public class ModuleCount {
	
	/**
	 * Get the configured Count PR, 
	 * which counts the number of times a sentiment word is said
	 * in a document according with our dictionaries.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public CountSentiment getCountTokens() throws Exception{
		CountSentiment count = new CountSentiment(); //Create the PR
		count.setName("Count"); //Set its name
		return count;
	}
	

}
