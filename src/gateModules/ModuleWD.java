package gateModules;
import java.io.File;
import java.util.ArrayList;

import gate.*;
import gate.creole.SerialAnalyserController;
import gate.creole.tokeniser.DefaultTokeniser;
import gate.gui.*;
import gate.creole.annotdelete.AnnotationDeletePR;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.ANNIETransducer;

public class ModuleWD{
	private final SerialAnalyserController controller = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
	
	public ModuleWD() throws Exception{
		this.controller.setName("ModuleWD");
	}
	
	public void add(ProcessingResource pr) throws Exception{
		this.controller.add(pr);
	}
	
	public void execute() throws Exception{
		this.controller.execute();
	}
	
	public void setCorpus(Corpus corpus) throws Exception{
		this.controller.setCorpus(corpus);
	}
	
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
	
	public static void main(String[] args) throws Exception{
	Gate.init(); // prepare the library
	MainFrame.getInstance().setVisible(true);

	// Example document to test the module
	Document document = Factory.newDocument("El valor de BBVA cae en bolsa otra vez");
	FeatureMap feats = Factory.newFeatureMap();
	feats.put("Date", "19-12-3013");
	document.setFeatures(feats);
	document.setName("Tweet de prueba");
	
	//For using ANNIE PR's
	// get the root plugins dir
	File pluginsDir = Gate.getPluginsHome();
	// Let's load the Tools plugin
	File aPluginDir = new File(pluginsDir, "ANNIE");
	// load the plugin.
	Gate.getCreoleRegister().registerDirectories(aPluginDir.toURI().toURL());
	
	ModuleWD module = new ModuleWD();
	//Delete PR
	AnnotationDeletePR delete = module.getDeletePR(); 
	//Annie Tokeniser 
	DefaultTokeniser tokeniser = module.getTokeniserPR();
	//Annie Gazetter
	DefaultGazetteer gazetteer = module.getGazetteerPR();
	//Annie NE Transducer
	ANNIETransducer transducer = module.getTransducerPR();
	//Adding the different PR.
	module.add(delete);
	module.add(tokeniser);
    module.add(gazetteer);
    module.add(transducer);
    Corpus corpus = Factory.newCorpus("Tweets");
    corpus.add(document); // add document to the corpus
    module.setCorpus(corpus); // set corpus
    module.execute(); // execute the corpus
	}
}
