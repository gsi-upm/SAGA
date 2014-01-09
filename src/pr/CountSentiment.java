package pr;

import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;

public class CountSentiment extends AbstractLanguageAnalyser {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/* (non-Javadoc)
 * @see gate.creole.AbstractProcessingResource#execute()
 */
@Override
public void execute() throws ExecutionException {
	int positive = document.getAnnotations("Sentiment").get("positive").size();
	System.out.println(document.getName() + " positive words " + positive);
	document.getFeatures().put("positive_count", positive);
	int negative = document.getAnnotations("Sentiment").get("negative").size();
	System.out.println(document.getName() + " negative words " + negative);
	document.getFeatures().put("positive_count", negative);
}

/* (non-Javadoc)
 * @see gate.creole.AbstractProcessingResource#init()
 */
@Override
public Resource init() throws ResourceInstantiationException {
	System.out.println(getClass().getName() + " ha sido cargado.");
	return this;
	}
}


