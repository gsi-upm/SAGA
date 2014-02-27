/**
 * This is the base module. It sets the corpus and the basics ANNIE's PR.
 * @author David Moreno Briz
 *
 */


package pr; //Package for the Processing Resources made by us.

import gate.Resource;
import gate.corpora.DocumentContentImpl;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;


public class CountSentiment extends AbstractLanguageAnalyser {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Only used in the Web Service, 
	 * because every GET petition will contain only one document (text) to analyze.
	 * 
	 * If at some point GET petitions include a set of documents,
	 * this variable will be of the type String[numberOfDocuments][2].
	 */
	private static String[] analysisResult;
	

/**
 * In local mode: 
 * it adds to a given document its numeric sentiment value and polarity.
 * 
 * In web service mode:
 * it saves in analysisResult the numeric sentiment value and the polarity of a given document.
 */
@Override
public void execute() throws ExecutionException {
	//Count how many positive annotations are in the Sentiment set of annotations in each document in the corpus
	int positive = document.getAnnotations("Sentiment").get("positive").size(); 
	//Count how many negative annotations are in the Sentiment set of annotations in each document in the corpus
	int negative = document.getAnnotations("Sentiment").get("negative").size();
	//Calculate the sentiment value (Goes from -1 to 1)
	double sentiment = 0; 
	if((positive + negative) != 0){ 
	sentiment = (positive - negative)/(positive + negative);
	}
	//Add results to the array
	analysisResult = new String[2];
	analysisResult[0] = Double.toString(sentiment);
	//Calculates polarity of the document
	if(sentiment > 0){
		analysisResult[1] = "Positive";
	} else if(sentiment < 0){
		analysisResult[1] = "Negative";
	} else{
		analysisResult[1] = "Neutral";
	}
	//Sets the sentiment value and polarity at the end of the document
	document.setContent(new DocumentContentImpl(document.getContent().toString() + " This text has a " + sentiment + " value and " + analysisResult[1] + " polarity."));
}

/**
 * Initialize the Count Sentiment Language Analyser. 
 */
@Override
public Resource init() throws ResourceInstantiationException {
	System.out.println(getClass().getName() + " is added to the controller.");
	return this;
	}

/**
 * @return analysisResult
 */
public static String[] getAnalysisResult(){
	return analysisResult;
}
}


