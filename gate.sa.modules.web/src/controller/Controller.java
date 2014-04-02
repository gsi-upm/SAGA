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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


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
          session.setAttribute("marlVisible", "hidden=\"true\"");
	    } else if (parameters.containsKey("input")){
	    	//Check that in not url or the other type
		  forward = ANALYZE_JSP;
		  String textToAnalize = request.getParameter("input");
		  try{
  			DictionaryBasedSentimentAnalyzer module = new DictionaryBasedSentimentAnalyzer("Easy sentiment count",this.getClass().getResource("/resources/gazetteer/finances/lists.def"));
  			Corpus corpus = Factory.newCorpus("Texto web");
  			Document textoWeb = Factory.newDocument(textToAnalize);
  			corpus.add(textoWeb);
  			module.setCorpus(corpus);
  			module.execute();
  			HttpSession session =request.getSession();
  			session.setAttribute("marlVisible", "");
            session.setAttribute("textToAnalize", textToAnalize);
            session.setAttribute("value", DictionaryBasedSentimentAnalyzer.getAnalysisResult()[0]);
            session.setAttribute("polarity", DictionaryBasedSentimentAnalyzer.getAnalysisResult()[1]);
            request.setAttribute("words", DictionaryBasedSentimentAnalyzer.getWordsAndValues());
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
