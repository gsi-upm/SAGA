/*******************************************************************************
 * Copyright (c) 2014 - David Moreno Briz - Grupo de Sistemas Inteligentes - Universidad Politécnica de Madrid. (GSI-UPM)
 * http://www.gsi.dit.upm.es/
 *  
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 *  
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and  limitations under the License.
 ******************************************************************************/
/**
 * Look at execute() to see what this PR does.
 * 
 * @author David Moreno Briz
 *
 */


package processingResources; //Package for the Processing Resources made by us.

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;

@CreoleResource(name = "Sentiment and emotion analysis calling SEAS", 
                comment = "Sentiment and emotion analysis calling SEAS") 
public class SentimentAnalysisCallingSEAS extends AbstractLanguageAnalyser {

	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Runtime parameter that sets the sentiment algorithm that the service is going to use.
	 */
	protected SentimentAlgorithm sentimentAlgorithm = SentimentAlgorithm.Dictionary;
	
	/**
	 * Runtime parameter that sets the sentiment dictionary that the service is going to use.
	 */
	protected SentimentDictionary sentimentDictionary = SentimentDictionary.AUTO;
	
	/**
	 * Runtime parameter that sets the emotion algorithm that the service is going to use.
	 */
	protected EmotionAlgorithm emotionAlgorithm = EmotionAlgorithm.auto;
	
	/**
	 * Runtime parameter that sets if the PR is going to perform sentiment analysis with the chosen algorithm.
	 */
	protected Boolean sentimentAnalysis = false;
	
	/**
	 * Runtime parameter that sets if the PR is going to perform emotion analysis with the chosen algorithm.
	 */
	protected Boolean emotionAnalysis = false;
	
	/**
	 * The Annotation Set that contains the annotation type to be analyzed
	 */
	private String inputASname;
	
	/**
	 * The annotation type to be analyzed
	 */
	private String annotationType;
	
	/**
	 * The endpoint of the sentiment analysis service.
	 */
	private String SentimentServiceURL = "http://demos.gsi.dit.upm.es/tomcat/SAGAtoNIF/Service";
	
	/**
	 * The endpoint of the emotion analysis service.
	 */
	private String EmotionServiceURL = "http://demos.gsi.dit.upm.es/onyxemote/emote.php?i=";
	
	/**
	 * Eurosentiment token to use their services
	 */
	private String EuroSentimentToken = "";
	
	private HashMap<String,String> dictionaries = new HashMap<String,String>();

	/**
	 * Initialize the PR. 
	 */
	@Override
	public Resource init() throws ResourceInstantiationException{
		System.out.println(getClass().getName() + " is added to the controller.");
		dictionaries.put("Spanish_finances_Paradigma", "spFinancial");
		dictionaries.put("English_finances_Loughran_McDonald", "enFinancial");
		dictionaries.put("Emoticon", "emoticon");
		dictionaries.put("Spanish_finances_and_Emoticon", "spFinancialEmoticon");
		dictionaries.put("English_finances_and_Emoticon", "enFinancialEmoticon");
		return this;
		}
	
	
/**
 * This PR perfoms sentiment and/or emotion analysis over a documents or a set of Annotations
 * by calling a web service developed by GSI called SEAS.
 */
@Override
public void execute() throws ExecutionException{
	if(this.getSentimentAnalysis() == true){ //If sentiment analysis is set to true
		this.callSentimentService(); //The PR performs sentiment analysis calling SEAS.
	}
	if(this.getEmotionAnalysis() == true){ //If emotions analysis is set to true
		this.callEmotionService(); //The PR performs emotion analysis calling SEAS.
	}
	
}


/**
 * 
 */
public void callSentimentService(){
	// We get the annotation set and the annotation type to we processed.
	AnnotationSet annotationSet = document.getAnnotations(this.getInputASname());
	AnnotationSet annotationSetByType = annotationSet.get(this.getAnnotationTypes());
	// Iterator whit all the annotation that matches our type.
	Iterator<Annotation> iteratorA = annotationSetByType.iterator();
	// The input to be analyzed.
	String input = "";
	// It there is no Annotation Set or Type that matches we process the whole document.
	if(annotationSet.isEmpty() || annotationSetByType.size() == 0){
		input = document.getContent().toString(); // We get the content to analyze
		this.processSentimentInput(input, null);
	}else{ // If not.
		while(iteratorA.hasNext()){ // We process each annotation.
			Annotation annotation = iteratorA.next();
			try{
				input = document.getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
				this.processSentimentInput(input, annotation);
			}catch(InvalidOffsetException e){
				System.out.println(e);
				//input = document.getContent().toString(); // We get the content to analyze
			}	
		}
	}
	
}

/**
 * @param input
 */
public void processSentimentInput(String input, Annotation annotation){
	String eurosentiment=""; // The JSON result of the service (parsed as a String) will be here
	String algo = "";
	if(this.getSentimentAlgorithm().toString().equalsIgnoreCase("Dictionary")){
		algo = this.dictionaries.get(this.getSentimentDictionary().toString());
			// If the algorithm is AUTO we try to detect the language and select the most apropiate 
		    if(algo.equalsIgnoreCase("auto")){
		    	FeatureMap languageFeatures = document.getFeatures();
		    	String language = "";
		    	try{
		    		language = (String) languageFeatures.get("lang");
		    	}catch(Exception e){
		    		System.out.println(e);
		    	}
		    	if(language.equalsIgnoreCase("spanish")){
		    		algo = "spFinancialEmoticon";
		    	} else if(language.equalsIgnoreCase("english")){
		    		algo = "enFinancialEmoticon";
		    	}else{
		    		algo = "emoticon";
		    	}
		    }
	}
	HttpEntity entity = this.callService(input, this.getSentimentServiceURL(), algo);	
    // Parse the JSON (HttpEntity) response into a String
   	eurosentiment = this.parseEntity(entity);
   	// We parse the JSON
    this.sentimentAnnotation(eurosentiment, annotation);
}

/**
 * @param input
 * @return
 */
public HttpEntity callService(String input, String ServiceURL, String algo){
	HttpEntity entity = null;
	// We prepare the HTTP call to SEAS
	HttpClient httpclient = HttpClients.createDefault();
    
    // NIF parameters.
    ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(4); // Prepare the request to the selected service.
   	params.add(new BasicNameValuePair("input", input));
   	params.add(new BasicNameValuePair("intype", "direct"));
   	params.add(new BasicNameValuePair("informat", "text"));
   	params.add(new BasicNameValuePair("outformat", "json-ld"));
   	params.add(new BasicNameValuePair("algo", algo));
    // Choose the selected service to make the HTTP call to SEAS.
    try{
    	HttpPost httppost = new HttpPost(ServiceURL);
    	httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
    	httppost.setHeader("x-eurosentiment-token", this.getEuroSentimentToken());
       	//Execute and get the response.
       	HttpResponse responseService = httpclient.execute(httppost);
       	entity = responseService.getEntity();
    }catch(Exception e){
   		System.err.println(e);
   	}
   	return entity;
}

/**
 * @param eurosentiment
 * @param annotation
 */
public void sentimentAnnotation(String eurosentiment, Annotation annotation){
	try {
    	JSONParser parser = new JSONParser();
    	try{
    		Object obj = parser.parse(eurosentiment); //Parse SEAS response.
    		JSONObject jsonObject = (JSONObject) obj; //Cast into JSON.
    		// We take entries
    		JSONArray entries = (JSONArray) jsonObject.get("entries");
    		Iterator<JSONObject> iterator = entries.iterator();
    		while (iterator.hasNext()) { // For each entry
    			JSONObject entrie = iterator.next();
    			// We take the text
    			//String context = (String) entrie.get("nif:isString");
        		//docContent = context;
    			// We parse the opinions of the text
    			JSONArray opinions = (JSONArray) entrie.get("opinions");
        		Iterator<JSONObject> iteratorOpinions = opinions.iterator();
        		while (iteratorOpinions.hasNext()) {
        			JSONObject opinion = iteratorOpinions.next();
        			// We take the polarity and the value of the text
        			Double textPolarityValue= (Double) opinion.get("marl:polarityValue");
        			String textHasPolarity = (String) opinion.get("marl:hasPolarity");
        			// We create the features for the processed input.
        			FeatureMap sentimentFeatures = Factory.newFeatureMap();
        			sentimentFeatures.put("polarity", textHasPolarity);
        			sentimentFeatures.put("value", textPolarityValue);
        			// We put the features in the annotation or in the document.
        			if(annotation != null){
        				annotation.setFeatures(sentimentFeatures);
        			}else{
        				document.setFeatures(sentimentFeatures);
        			}
        		}
        		// We take the words in the text with a sentiment value and polarity
//        		JSONArray strings = (JSONArray) entrie.get("strings");
//        		Iterator<JSONObject> iteratorStrings = strings.iterator();
//        		while (iteratorStrings.hasNext()) { // For each word we take its values.
//        			JSONObject string = iteratorStrings.next();
//        			
//        			String word= (String) string.get("nif:anchorOf");
//        			Long beginIndex = (Long) string.get("nif:beginIndex");
//        			Long endIndex = (Long) string.get("nif:endIndex");
//        			JSONObject stringOpinion = (JSONObject) string.get("opinions");
//        			Double stringPolarityValue= (Double) stringOpinion.get("marl:polarityValue");
//        			String stringHasPolarity = (String) stringOpinion.get("marl:hasPolarity");
//        		}	
    		}
    	}catch(Exception e){
    		System.out.println("The JSON is not parsed.");
    		System.out.println("The response of the service has been: " + eurosentiment);
    	}
	} catch (Exception e) {
		e.printStackTrace();
	}
}

/**
 * @param entity
 * @return
 */
public String parseEntity(HttpEntity entity){
	String eurosentiment = "";
	try{
    	if (entity != null) {
    		InputStream instream = entity.getContent();
    		try{
        	BufferedReader in = new BufferedReader(new InputStreamReader(instream));
    		String inputLine;
    		StringBuffer marl = new StringBuffer();
    		boolean knowPolarity = false; 
    		// Parse the service response into a String
    		while ((inputLine = in.readLine()) != null) {
    			marl.append(inputLine);
    			marl.append("\n");
    			// Change the color of the response box depending on the polarity of the analysis.
    			// The first "marl:Polarity" in the response will be the polarity of the text.
    			if(inputLine.contains("marl:Positive") && !knowPolarity){
    				knowPolarity = true;
    			}else if(inputLine.contains("marl:Negative") && !knowPolarity){
    				knowPolarity = true;
    			}else if(inputLine.contains("marl:Neutral") && !knowPolarity){
    				knowPolarity = true;
    			}
    			
    		}
    		in.close();
    		eurosentiment = marl.toString(); // Set the response.
    		}catch(Exception e){
        		System.err.println(e);
        	}finally{
        		try{
        			instream.close();
        		}catch(Exception e){
            		System.err.println(e);
            	}
    		}
    	}
    	}catch(Exception e){
    		System.err.println(e);
    	}
	return eurosentiment;
}


/**
 * 
 */
public void callEmotionService(){
	// We get the annotation set and the annotation type to we processed.
		AnnotationSet annotationSet = document.getAnnotations(this.getInputASname());
		AnnotationSet annotationSetByType = annotationSet.get(this.getAnnotationTypes());
		// Iterator whit all the annotation that matches our type.
		Iterator<Annotation> iteratorA = annotationSetByType.iterator();
		// The input to be analyzed.
		String input = "";
		// It there is no Annotation Set or Type that matches we process the whole document.
		if(annotationSet.isEmpty() || annotationSetByType.size() == 0){
			input = document.getContent().toString(); // We get the content to analyze
			this.processEmotionInput(input, null);
		}else{ // If not.
			while(iteratorA.hasNext()){ // We process each annotation.
				Annotation annotation = iteratorA.next();
				try{
					input = document.getContent().getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					this.processEmotionInput(input, annotation);
				}catch(InvalidOffsetException e){
					System.out.println(e);
					//input = document.getContent().toString(); // We get the content to analyze
				}	
			}
		}
}

public void processEmotionInput(String input, Annotation annotation){
	String eurosentiment=""; // The JSON result of the service (parsed as a String) will be here
	String algo = "";
	algo = this.getEmotionAlgorithm().toString();
	// If the algorithm is AUTO we try to detect the language and select the most apropiate 
	if(algo.equalsIgnoreCase("auto")){
		FeatureMap languageFeatures = document.getFeatures();
		String language = "";
    	try{
    		language = (String) languageFeatures.get("lang");
		}catch(Exception e){
		    System.out.println(e);
		}
		if(language.equalsIgnoreCase("spanish")){
		  	algo = "onyx";
		} else if(language.equalsIgnoreCase("english")){
		   	algo = "ANEW2010All";
	    }else{
	    	algo = "onyx";
	    }
	}
	HttpEntity entity = null;
	HttpClient httpclient = HttpClients.createDefault();
    // Choose the selected service.
    try{
    if (algo.equalsIgnoreCase("onyx")){
    	HttpGet httpget = new HttpGet("http://demos.gsi.dit.upm.es/onyxemote/emote.php?i=" + input.replace(' ', '+') + "&o=jsonld");
       	//Execute and get the response.
       	HttpResponse responseService = httpclient.execute(httpget);
       	entity = responseService.getEntity();
    }else{
    	entity = this.callService(input, this.getEmotionServiceURL(), algo);
    }
    }catch(Exception e){
    	System.err.println(e);
    }
   	// Parse the JSON (HttpEntity) response into a String
    eurosentiment = this.parseEntity(entity);
    // We parse the JSON
    this.emotionAnnotation(eurosentiment, annotation);
}

/**
 * @param eurosentiment
 * @param annotation
 */
public void emotionAnnotation(String eurosentiment, Annotation annotation){
	FeatureMap sentimentFeatures = Factory.newFeatureMap();
    try {
        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(eurosentiment); //Parse SEAS response.
            JSONObject jsonObject = (JSONObject) obj; //Cast into JSON.
            // We take entries
            if(jsonObject.get("entries") instanceof JSONObject){
            	JSONObject entries = (JSONObject) jsonObject.get("entries");
            	JSONObject emotionSet = (JSONObject) entries.get("onyx:hasEmotionSet");
            	sentimentFeatures = this.emotionSubParse(emotionSet, sentimentFeatures);
            }else if(jsonObject.get("entries") instanceof JSONArray){
            	JSONArray entries = (JSONArray) jsonObject.get("entries");
            	Iterator<JSONObject> iteratorEntries = entries.iterator();
            	while(iteratorEntries.hasNext()){
            		JSONObject entrie = (JSONObject) iteratorEntries.next();
            		JSONObject emotionSet = (JSONObject) entrie.get("onyx:hasEmotionSet");
                	sentimentFeatures = this.emotionSubParse(emotionSet, sentimentFeatures);
            	}
            }
            
            // We put the features in the annotation or in the document.
            if(annotation != null){
    			annotation.setFeatures(sentimentFeatures);
    		}else{
    			document.setFeatures(sentimentFeatures);
    		}
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("The JSON is not parsed.");
            System.out.println("The response of the service has been: " + eurosentiment);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public FeatureMap emotionSubParse(JSONObject emotionSet, FeatureMap sentimentFeatures){
	int i = 1;
	if(emotionSet.get("onyx:hasEmotion") instanceof JSONObject){
    	JSONObject emotions = (JSONObject) emotionSet.get("onyx:hasEmotion");
    	//Iterator<JSONObject> iteratorEmotions = emotions.iterator();
    	// while (iteratorEmotions.hasNext()) {
    	// JSONObject emotion = iteratorEmotions.next();
        // We take the polarity and the value of the text
        JSONObject category = (JSONObject) emotions.get("onyx:hasEmotionCategory");
        String id = (String) category.get("@id");
        JSONObject intensity = (JSONObject) emotions.get("onyx:hasEmotionIntensity");
        String value = (String) intensity.get("@value");
        // We create the features for the processed input.
        sentimentFeatures.put("emotion_" + i, id);
        sentimentFeatures.put("value_" + i, value);
    }else if(emotionSet.get("onyx:hasEmotion") instanceof JSONArray){
    	JSONArray emotions = (JSONArray) emotionSet.get("onyx:hasEmotion");
    	Iterator<JSONObject> iteratorEmotions = emotions.iterator();
    	while (iteratorEmotions.hasNext()) {
    		JSONObject emotion = iteratorEmotions.next();
        // We take the polarity and the value of the text
    		JSONObject category = (JSONObject) emotion.get("onyx:hasEmotionCategory");
    		String id = (String) category.get("@id");
    		JSONObject intensity = (JSONObject) emotion.get("onyx:hasEmotionIntensity");
    		String value = (String) intensity.get("@value");
    		// We create the features for the processed input.
    		sentimentFeatures.put("emotion_" + i, id);
            sentimentFeatures.put("value_" + i, value);
            i++;
    	}
    }
	return sentimentFeatures;
}

@Optional
@RunTime
@CreoleParameter(comment = "")
public void setSentimentAlgorithm(SentimentAlgorithm sentimentAlgorithm) {
	this.sentimentAlgorithm = sentimentAlgorithm;
}

public SentimentAlgorithm getSentimentAlgorithm(){
	return this.sentimentAlgorithm;
}

public EmotionAlgorithm getEmotionAlgorithm() {
	return emotionAlgorithm;
}

@Optional
@RunTime
@CreoleParameter(comment = "")
public void setEmotionAlgorithm(EmotionAlgorithm emotionAlgorithm) {
	this.emotionAlgorithm = emotionAlgorithm;
}


public Boolean getSentimentAnalysis() {
	return sentimentAnalysis;
}

@Optional
@RunTime
@CreoleParameter(comment = "")
public void setSentimentAnalysis(Boolean sentimentAnalysis) {
	this.sentimentAnalysis = sentimentAnalysis;
}

public Boolean getEmotionAnalysis() {
	return emotionAnalysis;
}

@Optional
@RunTime
@CreoleParameter(comment = "")
public void setEmotionAnalysis(Boolean emotionAnalysis) {
	this.emotionAnalysis = emotionAnalysis;
}

public String getInputASname() {
	return inputASname;
}

@Optional
@RunTime
@CreoleParameter(comment = "")
public void setInputASname(String inputASname) {
	this.inputASname = inputASname;
}

public String getAnnotationTypes() {
	return annotationType;
}

@Optional
@RunTime
@CreoleParameter(comment = "")
public void setAnnotationTypes(String annotationType) {
	this.annotationType = annotationType;
}

public String getSentimentServiceURL() {
	return SentimentServiceURL;
}

@Optional
@RunTime
@CreoleParameter(comment = "")
public void setSentimentServiceURL(String sentimentServiceURL) {
	this.SentimentServiceURL = sentimentServiceURL;
}


public SentimentDictionary getSentimentDictionary() {
	return sentimentDictionary;
}

@Optional
@RunTime
@CreoleParameter(comment = "")
public void setSentimentDictionary(SentimentDictionary sentimentDictionary) {
	this.sentimentDictionary = sentimentDictionary;
}


public String getEmotionServiceURL() {
	return EmotionServiceURL;
}

@Optional
@RunTime
@CreoleParameter(comment = "")
public void setEmotionServiceURL(String emotionServiceURL) {
	EmotionServiceURL = emotionServiceURL;
}


public String getEuroSentimentToken() {
	return EuroSentimentToken;
}

@Optional
@RunTime
@CreoleParameter(comment = "")
public void setEuroSentimentToken(String euroSentimentToken) {
	EuroSentimentToken = euroSentimentToken;
}



}

