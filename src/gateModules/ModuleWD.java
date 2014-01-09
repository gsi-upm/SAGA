package gateModules;
import pr.*;
import java.io.File;
import java.util.ArrayList;

import gate.*;
import gate.creole.SerialAnalyserController;
import gate.creole.tokeniser.DefaultTokeniser;
import gate.gui.*;
import gate.util.ExtensionFileFilter;
import gate.creole.annotdelete.AnnotationDeletePR;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.ANNIETransducer;

public class ModuleWD{
	
	/**
	 * This is the analyser controler where we will add all the Processing Resources for this module. 
	 */
	private final SerialAnalyserController controller = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
	
	/**
	 * Initialize the module giving it a name. 
	 * @throws Exception
	 */
	public ModuleWD() throws Exception{
		this.controller.setName("ModuleWD");
		//For using ANNIE PR's
		// get the root plugins dir
		File pluginsDir = Gate.getPluginsHome();
		// Let's load the Annie plugin
		File aPluginDir = new File(pluginsDir, "ANNIE");
		// load the plugin.
		Gate.getCreoleRegister().registerDirectories(aPluginDir.toURI().toURL());
		
		
		
		this.registerPrPlugin();
		//Delete PR
		AnnotationDeletePR delete = this.getDeletePR(); 
		//Annie Tokeniser 
		DefaultTokeniser tokeniser = this.getTokeniserPR();
		//Annie Gazetter
		DefaultGazetteer gazetteer = this.getGazetteerPR();
		//Annie NE Transducer
		ANNIETransducer transducer = this.getTransducerPR();
		//Count PR
		CountSentiment count = this.getCountTokens();
		//Adding the different PR.
		this.add(delete);
		this.add(tokeniser);
		this.add(gazetteer);
		this.add(transducer);
		this.add(count);
	    Corpus corpus = this.createCorpusAndPupulateIt();
	    this.setCorpus(corpus); // set corpus
	    
	}
	
	/**
	 * Add any Processing Resource designed 
	 * or configured by the user to the controller.
	 * 
	 * @param pr
	 * @throws Exception
	 */
	public void add(ProcessingResource pr) throws Exception{
		this.controller.add(pr);
	}
	
	/**
	 * Execute all the PRs in the controller.
	 * 
	 * @throws Exception
	 */
	public void execute() throws Exception{
		this.controller.execute();
	}
	
	/**
	 * Set the corpus of documents over which the controller will work. 
	 * @param corpus of xml documents to analyse.
	 * @throws Exception
	 */
	public void setCorpus(Corpus corpus) throws Exception{
		this.controller.setCorpus(corpus);
	}
	
	
	/**
	 * Create a corpus and populate it with the XML documents 
	 * in the directory resources/data/input.
	 * 
	 * @return the populated corpus
	 * @throws Exception
	 */
	public Corpus createCorpusAndPupulateIt() throws Exception{
		Corpus corpus = Factory.newCorpus("Tweets"); 
		ExtensionFileFilter filter = new ExtensionFileFilter("XML files", "xml");
		corpus.populate(this.getClass().getResource("/resources/data/input"), filter,"UTF-8", true);
		return corpus;
	}
	
	/**
	 * Get the configurated ANNIE Annotation Delete PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public AnnotationDeletePR getDeletePR() throws Exception{
		AnnotationDeletePR delete = new AnnotationDeletePR();
		delete.setName("Delete PR");
		delete.setKeepOriginalMarkupsAS(new Boolean(true));
		ArrayList<String> list = new ArrayList<String>();
		list.add("Key");
		delete.setSetsToKeep(list);
		delete.init();
		return delete;
	}
	
	/**
	 * Get the configurated ANNIE Tokeniser PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public DefaultTokeniser getTokeniserPR() throws Exception{
		DefaultTokeniser tokeniser = new DefaultTokeniser();
		tokeniser.setName("Tokenizator");
		tokeniser.setEncoding("UTF-8");
		tokeniser.setTokeniserRulesURL(this.getClass().getResource("/resources/tokeniser/DefaultTokeniser.rules"));
		tokeniser.setTransducerGrammarURL(this.getClass().getResource("/resources/tokeniser/postprocess.jape"));
		tokeniser.setAnnotationSetName("Entities");
		tokeniser.init();
		return tokeniser;
	}
	
	/**
	 * Get the configurated ANNIE Gazetteer PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public DefaultGazetteer getGazetteerPR() throws Exception{
		DefaultGazetteer gazetteer = new DefaultGazetteer();
		gazetteer.setName("Gazetter");
		gazetteer.setCaseSensitive(new Boolean(true));
		gazetteer.setEncoding("UTF-8");
		gazetteer.setListsURL(this.getClass().getResource("/resources/gazetteer/lists.def"));
		gazetteer.setAnnotationSetName("Entities");
		gazetteer.setLongestMatchOnly(new Boolean(true));
		gazetteer.setWholeWordsOnly(new Boolean(true));
		gazetteer.init();
		return gazetteer;
	}
	
	/**
	 * Get the configurated ANNIE Transducer PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public ANNIETransducer getTransducerPR() throws Exception{
		ANNIETransducer transducer = new ANNIETransducer();
		transducer.setName("NE Transducer");
		transducer.setEncoding("UTF-8");
		transducer.setGrammarURL(this.getClass().getResource("/resources/jape/main.jape"));
		transducer.setInputASName("Entities");
		transducer.setOutputASName("Sentiment");
		transducer.init();
		return transducer;
	}
	
	/**
	 * Get the configurated Count PR, 
	 * which counts the number of times a sentiment word is said
	 * in a document according with our dictionaries.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public CountSentiment getCountTokens() throws Exception{
		CountSentiment count = new CountSentiment();
		count.setName("Count");
		return count;
	}
	
	/**
	 * Register our own plugin for Count Sentiment PR 
	 * so we can use it in our controller.
	 *  
	 * @throws Exception
	 */
	public void registerPrPlugin() throws Exception{
		Gate.getCreoleRegister().registerDirectories(this.getClass().getResource("/pr/"));
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		
	Gate.init(); // prepare the library
	MainFrame.getInstance().setVisible(true);

	ModuleWD module = new ModuleWD();
    module.execute(); // execute the corpus
	}
}
