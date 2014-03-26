/**
 * An example that execute the module called MachineLearningBasedSentimentAnalysisModule over an example corpus in local/graphic mode.
 *
 * @author David Moreno Briz
 *
 */

package examples;

import java.io.File;
import java.util.ArrayList;

import gate.Corpus;
import gate.Factory;
import gate.Gate;
import gate.gui.MainFrame;
import gate.learning.RunMode;
import gateModules.MachineLearningBasedSentimentAnalysisModule;

public class MachineLearningTrainingAndTesting{
	
	/**
	 * Execute "MachineLearningBasedSentimentAnalysisModule" module in GATE graphic/local mode.
	 * 
	 * @param args not used
	 * @throws Exception
	 */
	
	public static void main(String[] args) throws Exception{
		Gate.init(); // Prepare the library
		MainFrame.getInstance().setVisible(true); //Set GATE app visible
		// For using ANNIE PR's
		// Get the root plugins dir
		File pluginsDir = Gate.getPluginsHome();
		// Load the Annie plugin
		File aPluginDir = new File(pluginsDir, "ANNIE");
		File aPluginDir2 = new File(pluginsDir, "Tools");
		File aPluginDir3 = new File(pluginsDir, "Learning");
		// Load the plugin
		Gate.getCreoleRegister().registerDirectories(aPluginDir.toURI().toURL());
		Gate.getCreoleRegister().registerDirectories(aPluginDir2.toURI().toURL());
		Gate.getCreoleRegister().registerDirectories(aPluginDir3.toURI().toURL());
		//Create DictionaryBasedSentimentAnalyzer and set the gazetteer that we are going to use in this example, which is about Spanish finances.
		ArrayList<String> list = new ArrayList<String>(); //List of sets to keep
		list.add("comment"); //Keeps Key set.
		MachineLearningBasedSentimentAnalysisModule trainingModule = new MachineLearningBasedSentimentAnalysisModule("Training","/resources/machineLearning/paum.xml", RunMode.TRAINING, "", list);
		//Create the corpus and populate it.
	    //Corpus corpus = module.createCorpusAndPupulateItExample();
		Corpus corpus = trainingModule.createCorpusAndPupulateIt("Training", "/resources/machineLearning/corpora/training");
		trainingModule.setCorpus(corpus); // Set corpus into the controller. 
		trainingModule.execute();
		MachineLearningBasedSentimentAnalysisModule applicationModule = new MachineLearningBasedSentimentAnalysisModule("Testing","/resources/machineLearning/paum.xml", RunMode.APPLICATION, "output", list);
		//Create the corpus and populate it.
	    //Corpus corpus = module.createCorpusAndPupulateItExample();
		Corpus corpus2 = applicationModule.createCorpusAndPupulateIt("Testing", "/resources/machineLearning/corpora/testing");
		applicationModule.setCorpus(corpus2); // Set corpus into the controller. 
		applicationModule.execute();
	}
}
