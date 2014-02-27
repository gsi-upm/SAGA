package pr; //Package for the Processing Resources made by us.

import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;


public class CountSentimentOfEachWord extends AbstractLanguageAnalyser {

	private static final long serialVersionUID = 1L;
	
	private static String[][] wordAndValues; //Suponemos que las peticiones get manda las frases de una en una
	

/**
 * Save in resultadoAnalisis the value and the polarity of a given document.
 */
@Override
public void execute() throws ExecutionException {
	String text = document.getContent().toString(); //SAco el texto del documento
	String wordsInText[] = text.split(" "); //Extraigo las palabras del texto
	wordAndValues = new String[wordsInText.length][5];
	int position = 0; //Var auxiliar para ver donde empieza y acaba cada palabra dentro del texto
	for(int i = 0; i < wordsInText.length; i++){ //Para cada palabra en el texto
		wordAndValues[i][0] = wordsInText[i]; //Guardo la palabra
		wordAndValues[i][1] = Integer.toString(position); //La posicion en la que empieza
		position += (wordsInText[i].length() - 1); //La posicion en la que acaba
		wordAndValues[i][2] = Integer.toString(position);
		position += 2; //donde empieza la siguiente palabra
		int positive = document.getAnnotations("Sentiment").get(new Long(wordAndValues[i][1]), new Long(wordAndValues[i][2])).get("positive").size();
		int negative = document.getAnnotations("Sentiment").get(new Long(wordAndValues[i][2]), new Long(wordAndValues[i][2])).get("negative").size();
		if(positive > 0){
			wordAndValues[i][3] = "1.0";
			wordAndValues[i][4] = "Positive";
		} else if (negative > 0){
			wordAndValues[i][3] = "-1.0";
			wordAndValues[i][4] = "Negative";
		} else{
			wordAndValues[i][3] = "0.0";
			wordAndValues[i][4] = "Neutral";
		}
	}
}

/**
 * Initialize the Count Sentiment Of Each Word Language Analyser. 
 */
@Override
public Resource init() throws ResourceInstantiationException {
	System.out.println(getClass().getName() + " is added to the controller.");
	return this;
	}

public static String[][] getWordsAndValues(){
	return wordAndValues;
}
}


