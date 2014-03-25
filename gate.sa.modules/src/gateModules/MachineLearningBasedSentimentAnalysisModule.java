package gateModules; //Package for the different modules

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import gate.*;
import gate.creole.POSTagger;
import gate.creole.SerialAnalyserController;
import gate.creole.morph.Morph;
import gate.creole.splitter.SentenceSplitter;
import gate.creole.tokeniser.DefaultTokeniser;
import gate.util.ExtensionFileFilter;
import gate.creole.annotdelete.AnnotationDeletePR;
import gate.creole.annotransfer.AnnotationSetTransfer;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.ANNIETransducer;

public class MachineLearningBasedSentimentAnalysisModule{
	
	/**
	 * This is the analyser controler where we will add all the Processing Resources for this module. 
	 */
	private final SerialAnalyserController controller = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
	
	/**
	 * Constructor of the base module called MachineLearningBasedSentimentAnalysisModule.
	 * 
	 * 
	 * @param name name of the module
	 * @param listsURL location of the lists to set the gazetteer. In URL format.
	 * @throws Exception
	 */
	public MachineLearningBasedSentimentAnalysisModule(String name) throws Exception{
		this.controller.setName(name); // Set the module name
		//Delete PR.
		AnnotationDeletePR delete = this.getDeletePR(); 
		//Annie Tokeniser. 
		DefaultTokeniser tokeniser = this.getTokeniserPR();
		
		//Adding the different PR.
		this.add(delete);
		this.add(this.getAnnotationSetTransferPR());
		this.add(tokeniser);
		this.add(this.getSentenceSplitterPR());
		this.add(this.getPOSTaggerPR());
		this.add(this.getMorphologicalAnalyserPR());
	}
	
	/**
	 * Add any Processing Resource designed 
	 * or configured by the user to the controller.
	 * 
	 * @param processingResources
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
	 * in the directory /resources/machineLearning/corpora/training.
	 * 
	 * @return the populated corpus
	 * @throws Exception
	 */
	public Corpus createCorpusAndPupulateItTraining() throws Exception{
		Corpus corpus = Factory.newCorpus("Tweets"); //Create a corpus name Tweets
		ExtensionFileFilter filter = new ExtensionFileFilter("XML files", "xml"); //A filter to add XML documents
		corpus.populate(this.getClass().getResource("/resources/machineLearning/corpora/training"), filter,"UTF-8", true); //Populate it from /resource/data/input directory
		return corpus;
	}
	
	/**
	 * Get the configured ANNIE Annotation Delete PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public AnnotationDeletePR getDeletePR() throws Exception{
		AnnotationDeletePR delete = new AnnotationDeletePR(); //Create the PR
		delete.setName("Document Reset PR"); //Set its name
		delete.setKeepOriginalMarkupsAS(new Boolean(true)); //Keep the original XML markups of the documents in the corpus 
		ArrayList<String> list = new ArrayList<String>(); //List of sets to keep
		list.add("Key"); //Keeps Key set.
		delete.setSetsToKeep(list);
		delete.init(); // The PR is initialized.
		return delete;
	}
	
	/**
	 * Get the configured Annotation Set Transfer PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public AnnotationSetTransfer getAnnotationSetTransferPR() throws Exception{
		AnnotationSetTransfer pr = new AnnotationSetTransfer(); //Create the PR
		pr.setName("Annotation Set Transfer PR"); //Set its name
		//Set tonekiser rules from the file in /resources/tokeniser/DefaultTokeniser.rules
		pr.setCopyAnnotations(true);
		pr.setTransferAllUnlessFound(true);
		ArrayList<String> list = new ArrayList<String>(); //List of sets to keep
		list.add("comment"); //Keeps Key set.
		pr.setAnnotationTypes(list);
		pr.setInputASName("Key");
		pr.setOutputASName("");
		pr.setTagASName("");
		pr.setTextTagName("");
		pr.init();
		return pr;
	}
	
	/**
	 * Get the configured ANNIE Tokeniser PR.
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
		//tokeniser.setAnnotationSetName(""); //Set annotation set name for the token annotation.
		tokeniser.init(); //The PR is initialized
		return tokeniser;
	}
	
	
	/**
	 * Get the configured ANNIE Sentence Splitter PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public SentenceSplitter getSentenceSplitterPR() throws Exception{
		SentenceSplitter pr = new SentenceSplitter(); //Create the PR
		pr.setName("Sentence Splitter PR"); //Set its name 
		pr.setEncoding("UTF-8");
		//Set the list of the dictionaries that are going to be used by the Gazetteer
		pr.setTransducerURL(this.getClass().getResource("/resources/sentenceSplitter/grammar/main.jape"));
		pr.setGazetteerListsURL(this.getClass().getResource("/resources/sentenceSplitter/gazetteer/lists.def"));
		//Set annotation set name for the gazetteer features.
		pr.init(); //The PR is initialized
		return pr;
	}
	
	/**
	 * Get the configured ANNIE POS Tagger PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public POSTagger getPOSTaggerPR() throws Exception{
		POSTagger pr = new POSTagger(); //Create the PR
		pr.setName("POS Tagger PR"); //Set its name
		pr.setEncoding("UTF-8");
		//Set the grammar to transduce the features into annotations.
		pr.setLexiconURL(this.getClass().getResource("/resources/sentenceSplitter/gazetteer/lists.def"));
		pr.setRulesURL(this.getClass().getResource("/resources/heptag/ruleset"));
		pr.setBaseSentenceAnnotationType("Sentence");
		pr.setBaseTokenAnnotationType("Token");
		pr.setFailOnMissingInputAnnotations(true);
		pr.setInputASName("");
		pr.setOutputASName("");
		pr.setOutputAnnotationType("Token");
		pr.setPosTagAllTokens(true);
		pr.init(); //The PR is initialized
		return pr;
	}
	
	/**
	 * Get the configured GATE MorphologicalAnalyser PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
	public Morph getMorphologicalAnalyserPR() throws Exception{
		Morph pr = new Morph(); //Create the PR
		pr.setName("MorphologicalAnalyser PR"); //Set its name
		//Set the grammar to transduce the features into annotations.
		pr.setRulesFile(this.getClass().getResource("/resources/morph/default.rul"));
		pr.setCaseSensitive(false);
		pr.setAffixFeatureName("affix");
		pr.setAnnotationSetName("");
		pr.setConsiderPOSTag(true);
		pr.setFailOnMissingInputAnnotations(true);
		pr.setRootFeatureName("root");
		pr.init(); //The PR is initialized
		return pr;
	}
	
	/**
	 * Get the configured GATE Batch PR.
	 * 
	 * @return the initialized PR.
	 * @throws Exception
	 */
//  public Learning getMachineLearningPR() throws Exception{
//		File configFile = new File("/home/you/ml_config.xml"); //Wherever it is 
//		RunMode mode = RunMode.EVALUATION; //or TRAINING, or APPLICATION .. 
//		//Set up the PR and add it to the pipeline. 
//		//As with using the PR from GATE Developer, it needs a config file 
//		//and a mode. 
//		FeatureMap fm = Factory.newFeatureMap(); 
//		fm.put("configFileURL", configFile.toURI().toURL()); 
//		fm.put("learningMode", mode); 
//		gate.learning.LearningAPIMain learner = 
//		        (gate.learning.LearningAPIMain) 
//		        gate.Factory.createResource("gate.learning.LearningAPIMain", fm); 
//	}

}
