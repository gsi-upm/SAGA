package gateModules; //Package for the different modules

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
		this.controller.setName("ModuleWD"); // Set the module name
		// For using ANNIE PR's
		// Get the root plugins dir
		File pluginsDir = Gate.getPluginsHome();
		// Let's load the Annie plugin
		File aPluginDir = new File(pluginsDir, "ANNIE");
		// Load the plugin.
		Gate.getCreoleRegister().registerDirectories(aPluginDir.toURI().toURL());
		//Register our own plugig to use the count PR
		this.registerPrPlugin();
		//Delete PR.
		AnnotationDeletePR delete = this.getDeletePR(); 
		//Annie Tokeniser. 
		DefaultTokeniser tokeniser = this.getTokeniserPR();
		//Annie Gazetter.
		DefaultGazetteer gazetteer = this.getGazetteerPR();
		//Annie NE Transducer.
		ANNIETransducer transducer = this.getTransducerPR();
		//Count PR.
		CountSentiment count = this.getCountTokens();
		//Adding the different PR.
		this.add(delete);
		this.add(tokeniser);
		this.add(gazetteer);
		this.add(transducer);
		this.add(count);
		//Create the corpus and populate it.
	    Corpus corpus = this.createCorpusAndPupulateIt();
	    this.setCorpus(corpus); // Set corpus into the controller.
	    
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
		Corpus corpus = Factory.newCorpus("Tweets"); //Create a corpus name Tweets
		ExtensionFileFilter filter = new ExtensionFileFilter("XML files", "xml"); //A filter to add XML documents
		corpus.populate(this.getClass().getResource("/resources/data/input"), filter,"UTF-8", true); //Populate it from /resource/data/input directory
		return corpus;
	}
	
	/**
	 * Get the configurated ANNIE Annotation Delete PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public AnnotationDeletePR getDeletePR() throws Exception{
		AnnotationDeletePR delete = new AnnotationDeletePR(); //Create the PR
		delete.setName("Delete PR"); //Set its name
		delete.setKeepOriginalMarkupsAS(new Boolean(true)); //Keep the original XML markups of the documents in the corpus 
		ArrayList<String> list = new ArrayList<String>(); //List of sets to keep
		list.add("Key"); //Keeps Key set.
		delete.setSetsToKeep(list);
		delete.init(); // The PR is initialized.
		return delete;
	}
	
	/**
	 * Get the configurated ANNIE Tokeniser PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public DefaultTokeniser getTokeniserPR() throws Exception{
		DefaultTokeniser tokeniser = new DefaultTokeniser(); //Create the PR
		tokeniser.setName("Tokenizator"); //Set its name
		tokeniser.setEncoding("UTF-8"); //Set encoding
		//Set tonekiser rules from the file in /resources/tokeniser/DefaultTokeniser.rules
		tokeniser.setTokeniserRulesURL(this.getClass().getResource("/resources/tokeniser/DefaultTokeniser.rules"));
		//And the grammar from /resources/tokeniser/postprocess.jape
		tokeniser.setTransducerGrammarURL(this.getClass().getResource("/resources/tokeniser/postprocess.jape"));
		tokeniser.setAnnotationSetName("Entities"); //Set annotation set name for the token annotation.
		tokeniser.init(); //The PR is initialized
		return tokeniser;
	}
	
	/**
	 * Get the configurated ANNIE Gazetteer PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public DefaultGazetteer getGazetteerPR() throws Exception{
		DefaultGazetteer gazetteer = new DefaultGazetteer(); //Create the PR
		gazetteer.setName("Gazetter"); //Set its name
		gazetteer.setCaseSensitive(new Boolean(true)); 
		gazetteer.setEncoding("UTF-8");
		//Set the list of the dictionaries that are going to be used by the Gazetteer
		gazetteer.setListsURL(this.getClass().getResource("/resources/gazetteer/lists.def"));
		//Set annotation set name for the gazetteer features.
		gazetteer.setAnnotationSetName("Entities");
		gazetteer.setLongestMatchOnly(new Boolean(true));
		gazetteer.setWholeWordsOnly(new Boolean(true));
		gazetteer.init(); //The PR is initialized
		return gazetteer;
	}
	
	/**
	 * Get the configurated ANNIE Transducer PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public ANNIETransducer getTransducerPR() throws Exception{
		ANNIETransducer transducer = new ANNIETransducer(); //Create the PR
		transducer.setName("NE Transducer"); //Set its name
		transducer.setEncoding("UTF-8");
		//Set the grammar to transduce the features into annotations.
		transducer.setGrammarURL(this.getClass().getResource("/resources/jape/main.jape"));
		transducer.setInputASName("Entities"); //Input set of annotations to run the transducer
		transducer.setOutputASName("Sentiment"); //Output annotation
		transducer.init(); //The PR is initialized
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
		CountSentiment count = new CountSentiment(); //Create the PR
		count.setName("Count"); //Set its name
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
	 * Execute the module in GATE graphic mode.
	 * 
	 * @param args not used
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		
	Gate.init(); // Prepare the library
	MainFrame.getInstance().setVisible(true); //Set GATE app visible
	//Create the module with the controller, configurated PRs and populated corpus
	ModuleWD module = new ModuleWD(); 
    module.execute(); // And execute it
	}
}
