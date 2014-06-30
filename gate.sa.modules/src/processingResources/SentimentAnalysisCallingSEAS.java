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
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import gate.Factory;
import gate.Resource;
import gate.corpora.DocumentContentImpl;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.annic.apache.lucene.document.Document;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

@CreoleResource(name = "Sentiment and emotion analysis calling SEAS", 
                comment = "Sentiment and emotion analysis calling SEAS") 
public class SentimentAnalysisCallingSEAS extends AbstractLanguageAnalyser {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Only used in the Web Service, 
	 * because every GET petition will contain only one document (text) to analyze.
	 * 
	 * If at some point GET petitions include a set of documents,
	 * this variable will be of the type String[numberOfDocuments][2].
	 */
	private String[] analysisResult;
	
	protected Algorithm algorithm = Algorithm.spFinancialEmoticon;

	protected URL src;

	protected String content;
	

/**
 * In local mode: 
 * it adds to a given document its numeric sentiment value and polarity.
 * 
 * In web service mode:
 * it saves in analysisResult the numeric sentiment value and the polarity of a given document.
 */
@Override
public void execute() throws ExecutionException{
	String eurosentiment=""; // The result of the service will be here
	String input = document.getContent().toString();
	HttpEntity entity = null;
	HttpClient httpclient = HttpClients.createDefault();
    HttpPost httppost = new HttpPost("http://demos.gsi.dit.upm.es/tomcat/SAGAtoNIF/Service"); // Default service to be call.
    // Request parameters and other properties.
    	//String algo = request.getParameter("algo"); // Get the algorithm name.
    	ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(4); // Prepare the request to the selected service.
    	params.add(new BasicNameValuePair("input", input));
    	params.add(new BasicNameValuePair("intype", "direct"));
    	params.add(new BasicNameValuePair("informat", "text"));
    	params.add(new BasicNameValuePair("outformat", "json-ld"));
    	params.add(new BasicNameValuePair("algo", this.getAlgorithm().toString()));
    	// Choose the selected service.
    	try{
    	String algo = this.getAlgorithm().toString();
    	if (algo.equalsIgnoreCase("spFinancial") || algo.equalsIgnoreCase("spFinancialEmoticon") || algo.equalsIgnoreCase("Emoticon")){
    		httppost = new HttpPost("http://demos.gsi.dit.upm.es/tomcat/SAGAtoNIF/Service");
    		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        	//Execute and get the response.
        	HttpResponse responseService = httpclient.execute(httppost);
        	entity = responseService.getEntity();
    	} else if (algo.equalsIgnoreCase("enFinancial") || algo.equalsIgnoreCase("enFinancialEmoticon") || algo.equalsIgnoreCase("ANEW2010All") || algo.equalsIgnoreCase("ANEW2010Men") || algo.equalsIgnoreCase("ANEW2010Women")){
    		httppost = new HttpPost("http://demos.gsi.dit.upm.es/tomcat/RestrictedToNIF/RestrictedService");
    		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        	//Execute and get the response.
        	HttpResponse responseService = httpclient.execute(httppost);
        	entity = responseService.getEntity();
    	} else if (algo.equalsIgnoreCase("onyx")){
    		HttpGet httpget = new HttpGet("http://demos.gsi.dit.upm.es/onyxemote/emote.php?i=" + input.replace(' ', '+') + "&o=jsonld");
        	//Execute and get the response.
        	HttpResponse responseService = httpclient.execute(httpget);
        	entity = responseService.getEntity();
    	}
    	}catch(Exception e){
    		System.err.println(e);
    	}
    	
    	// Parse the response
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
    	
    //document.setContent(new DocumentContentImpl(document.getContent().toString() + "\n\n" + ((eurosentiment.replace("&quot;", "\"")).replace("</p></body></html>", "")).replace("<html><body><p>","")));
    try {
		Factory.newDocument(document.getContent().toString() + "\n\n" + ((eurosentiment.replace("&quot;", "\"")).replace("</p></body></html>", "")).replace("<html><body><p>",""));
		//corpus.add(doc);
	} catch (ResourceInstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
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
public String[] getAnalysisResult(){
	return analysisResult;
}


@Optional
@RunTime
@CreoleParameter(comment = "The location of the list of abbreviations")
public void setAlgorithm(Algorithm algorithm) {
	this.algorithm = algorithm;
}

public Algorithm getAlgorithm(){
	return this.algorithm;
}

}

