package pr; //Package for the Processing Resources made by us.

import gate.Resource;
import gate.corpora.DocumentContentImpl;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;


public class CountSentiment extends AbstractLanguageAnalyser {

	private static final long serialVersionUID = 1L;
	
	private static String[] resultadoAnalisis; //Nos vale de una dimension porque mandadmos las frases de una en una en cada peticion get
	

/**
 * Save in resultadoAnalisis the value and the polarity of a given document.
 */
@Override
public void execute() throws ExecutionException {
	//Count how many positive annotations are in the Sentiment set of annotations in each document in the corpus
	int positive = document.getAnnotations("Sentiment").get("positive").size(); 
	//Count how many negative annotations are in the Sentiment set of annotations in each document in the corpus
	int negative = document.getAnnotations("Sentiment").get("negative").size();
	double sentiment = 0; 
	if((positive + negative) != 0){ //An easy count of the sent value (Goes from -1 to 1)
	sentiment = (positive - negative)/(positive + negative);
	}
	//Sets the sent value at the end of the document
	resultadoAnalisis = new String[2];
	resultadoAnalisis[0] = Double.toString(sentiment);
	if(sentiment > 0){
		resultadoAnalisis[1] = "Positive";
	} else if(sentiment < 0){
		resultadoAnalisis[1] = "Negative";
	} else{
	resultadoAnalisis[1] = "Neutral";
	}
	document.setContent(new DocumentContentImpl(document.getContent().toString() + " This text has a " + sentiment + " value"));
}

/**
 * Initialize the Count Sentiment Language Analyser. 
 */
@Override
public Resource init() throws ResourceInstantiationException {
	System.out.println(getClass().getName() + " is added to the controller.");
	return this;
	}

public static String[] resultadoAnalisis(){
	return resultadoAnalisis;
}
}


