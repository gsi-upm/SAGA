/*******************************************************************************
 * Copyright (c) 2014 - David Moreno Briz - Grupo de Sistemas Inteligentes - Universidad Polit?cnica de Madrid. (GSI-UPM)
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
package controller;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gateModules.DictionaryBasedSentimentAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;



/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String ANALYZE_JSP = "/analyze.jsp";
	private static String HOME_JSP = "/home.jsp";
	private static boolean gateInited = false; 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Controller() {
        super();
    }
    
    
    
      public void init() throws ServletException { 
        if(!gateInited) { 
          try { 
            ServletContext ctx = getServletContext(); 
     
            // use /path/to/your/webapp/WEB-INF as gate.home 
            File gateHome = new File(ctx.getRealPath("/WEB-INF")); 
            
            
     
            Gate.setGateHome(gateHome); 
            // thus webapp/WEB-INF/plugins is the plugins directory, and 
            // webapp/WEB-INF/gate.xml is the site config file. 
     
            // Use webapp/WEB-INF/user-gate.xml as the user config file, 
            //  to avoid confusion with your own user config. 
            Gate.setUserConfigFile(new File(gateHome, "user-gate.xml")); 
     
            Gate.init(); 
            // load plugins, for example... 
            
            Gate.getCreoleRegister().registerDirectories( 
                    ctx.getResource("/WEB-INF/plugins/ANNIE")); 
            
            Gate.getCreoleRegister().registerDirectories( 
                    ctx.getResource("/WEB-INF/plugins/processingResources")); 
            Gate.getCreoleRegister().registerDirectories( 
                    ctx.getResource("/WEB-INF/plugins/webProcessingResources")); 
     
            gateInited = true; 
          
        } 
        catch(Exception ex) { 
            throw new ServletException("Exception initialising GATE", ex); 
          } 
        } 
    } 

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String forward="";
	    // Get a map of the request parameters
	    @SuppressWarnings("unchecked")
	    Map parameters = request.getParameterMap();
	    if (parameters.containsKey("analyze")){
	      forward = ANALYZE_JSP;
	      HttpSession session =request.getSession();
	      session.setAttribute("textToAnalize", "");
	      session.setAttribute("value", "");
          session.setAttribute("polarity", "");
          session.setAttribute("eurosentiment", "");
          session.setAttribute("marlVisible", "hidden=\"true\"");
	    } else if (parameters.containsKey("input")){
	    	//Check that in not url or the other type
		  forward = ANALYZE_JSP;
		  String textToAnalize = request.getParameter("input");
		  try{
			ArrayList<URL> dictionaries = new ArrayList<URL>();
			dictionaries.add((new Controller()).getClass().getResource("/resources/gazetteer/emoticon/lists.def"));
			dictionaries.add((new Controller()).getClass().getResource("/resources/gazetteer/finances/spanish/paradigma/lists.def"));
			DictionaryBasedSentimentAnalyzer module = new DictionaryBasedSentimentAnalyzer("SAGA - Emoticon Sentiment Analyzer", dictionaries);
  			Corpus corpus = Factory.newCorpus("Texto web");
  			Document textoWeb = Factory.newDocument(textToAnalize);
  			corpus.add(textoWeb);
  			module.setCorpus(corpus);
  			module.execute();
            //Calling MARL generator
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("http://demos.gsi.dit.upm.es/eurosentiment/marlgenerator/process");

            // Request parameters and other properties.
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(4);
            params.add(new BasicNameValuePair("intype", "direct"));
            params.add(new BasicNameValuePair("informat", "GALA"));
            params.add(new BasicNameValuePair("outformat", "jsonld"));
            StringBuffer input = new StringBuffer();
            input.append(textToAnalize);
            input.append("	");
            input.append(DictionaryBasedSentimentAnalyzer.getAnalysisResult()[1]);
            input.append("	");
            input.append(DictionaryBasedSentimentAnalyzer.getAnalysisResult()[0]);
            String[][] words = DictionaryBasedSentimentAnalyzer.getWordsAndValues();
            for(int i = 0; i < words.length; i++){
            	if(words[i][4].equals("Neutral") == false){
            		input.append("	");
            		input.append(words[i][0]);
            		input.append("	");
            		input.append(words[i][1]);
            		input.append("	");
            		input.append(words[i][2]);
            		input.append("	");
            		input.append(words[i][4]);
            		input.append("	");
            		input.append(words[i][3]);
            	}
            }
            params.add(new BasicNameValuePair("input", input.toString()));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Execute and get the response.
            HttpResponse responseMARL = httpclient.execute(httppost);
            HttpEntity entity = responseMARL.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                	BufferedReader in = new BufferedReader(new InputStreamReader(instream));
            		String inputLine;
            		StringBuffer marl = new StringBuffer();
             
            		while ((inputLine = in.readLine()) != null) {
            			marl.append(inputLine);
            			marl.append("\n");
            		}
            		in.close();
             
            		HttpSession session =request.getSession();
            		session.setAttribute("marlVisible", "");
            		session.setAttribute("eurosentiment", marl.toString());
                } finally {
                    instream.close();
                }
            }
  			} catch(Exception e){
  				System.out.println("It does not execute.");
  			}
	    } else {
	      forward = HOME_JSP;
	      HttpSession session =request.getSession();
	      session.setAttribute("textToAnalize", "");
	      session.setAttribute("value", "");
          session.setAttribute("polarity", "");
          session.setAttribute("marlVisible", "hidden=\"true\"");
	    }
	    RequestDispatcher view = request.getRequestDispatcher(forward);
	    view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
