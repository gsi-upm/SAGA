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
	private final SerialAnalyserController controller = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
	
	/**
	 * @throws Exception
	 */
	public ModuleWD() throws Exception{
		this.controller.setName("ModuleWD");
	}
	
	/**
	 * @param pr
	 * @throws Exception
	 */
	public void add(ProcessingResource pr) throws Exception{
		this.controller.add(pr);
	}
	
	/**
	 * @throws Exception
	 */
	public void execute() throws Exception{
		this.controller.execute();
	}
	
	/**
	 * @param corpus
	 * @throws Exception
	 */
	public void setCorpus(Corpus corpus) throws Exception{
		this.controller.setCorpus(corpus);
	}
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public Corpus createCorpusAndPupulateIt() throws Exception{
		Corpus corpus = Factory.newCorpus("Tweets"); 
		ExtensionFileFilter filter = new ExtensionFileFilter("XML files", "xml");
		corpus.populate(this.getClass().getResource("/resources/data/input"), filter,"UTF-8", true);
		return corpus;
	}
	
	/**
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
	 * @throws Exception
	 */
	public CountSentiment getCountTokens() throws Exception{
		CountSentiment count = new CountSentiment();
		count.setName("Count");
		return count;
	}
	
	/**
	 * @throws Exception
	 */
	public void registerPrPlugin() throws Exception{
		Gate.getCreoleRegister().registerDirectories(this.getClass().getResource("/pr/"));
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
	Gate.init(); // prepare the library
	MainFrame.getInstance().setVisible(true);

	// Example document to test the module
//	Document document = Factory.newDocument("El valor de BBVA cae en bolsa otra vez");
//	FeatureMap feats = Factory.newFeatureMap();
//	feats.put("Date", "19-12-3013");
//	document.setFeatures(feats);
//	document.setName("Tweet de prueba");
	
	//For using ANNIE PR's
	// get the root plugins dir
	File pluginsDir = Gate.getPluginsHome();
	// Let's load the Annie plugin
	File aPluginDir = new File(pluginsDir, "ANNIE");
	// load the plugin.
	Gate.getCreoleRegister().registerDirectories(aPluginDir.toURI().toURL());
	
	ModuleWD module = new ModuleWD();
	
	module.registerPrPlugin();
	//Delete PR
	AnnotationDeletePR delete = module.getDeletePR(); 
	//Annie Tokeniser 
	DefaultTokeniser tokeniser = module.getTokeniserPR();
	//Annie Gazetter
	DefaultGazetteer gazetteer = module.getGazetteerPR();
	//Annie NE Transducer
	ANNIETransducer transducer = module.getTransducerPR();
	//Count PR
	CountSentiment count = module.getCountTokens();
	//Adding the different PR.
	module.add(delete);
	module.add(tokeniser);
    module.add(gazetteer);
    module.add(transducer);
    module.add(count);
    Corpus corpus = module.createCorpusAndPupulateIt();
    module.setCorpus(corpus); // set corpus
    module.execute(); // execute the corpus
	}
}
