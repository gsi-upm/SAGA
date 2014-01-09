package pr; //Package for the Processing Resources made by us.

import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;

public class CountSentiment extends AbstractLanguageAnalyser {

	private static final long serialVersionUID = 1L;

/**
 * What the PR does when it executed.
 */
@Override
public void execute() throws ExecutionException {
	//Count how many positive annotations are in the Sentiment set of annotations in each document in the corpus
	int positive = document.getAnnotations("Sentiment").get("positive").size(); 
	System.out.println(document.getName() + " positive words " + positive); //Print them.
	document.getFeatures().put("positive_count", positive); //Annotate the count as a feature.
	//Count how many negative annotations are in the Sentiment set of annotations in each document in the corpus
	int negative = document.getAnnotations("Sentiment").get("negative").size();
	System.out.println(document.getName() + " negative words " + negative); //Print them.
	document.getFeatures().put("positive_count", negative); //Annotate the count as a feature.
}

/**
 * Initialize the Count Sentiment Language Analyser. 
 */
@Override
public Resource init() throws ResourceInstantiationException {
	System.out.println(getClass().getName() + " is added to the controller.");
	return this;
	}
}


