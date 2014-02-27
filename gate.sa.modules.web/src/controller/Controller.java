package controller;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gateModules.ModuleCount;

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
                    ctx.getResource("/WEB-INF/plugins/pr")); 
     
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
	    } else if (parameters.containsKey("input")){
	    	//Check that in not url or the other type
		  forward = ANALYZE_JSP;
		  String textToAnalize = request.getParameter("input");
		  try{
  			ModuleCount module = new ModuleCount("Easy sentiment count");
  			Corpus corpus = Factory.newCorpus("Texto web");
  			Document textoWeb = Factory.newDocument(textToAnalize);
  			corpus.add(textoWeb);
  			//Create the PR we want to add to ModuleWD module.
  			module.setCorpus(corpus);
  			module.execute();
  			HttpSession session =request.getSession();
            session.setAttribute("textToAnalize", textToAnalize);
            session.setAttribute("value", ModuleCount.getAnalysisResult()[0]);
            session.setAttribute("polarity", ModuleCount.getAnalysisResult()[1]);
            request.setAttribute("words", ModuleCount.getWordsAndValues());
  			} catch(Exception e){
  				System.out.println("It does not execute.");
  			}
	    } else {
	      forward = HOME_JSP;
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
