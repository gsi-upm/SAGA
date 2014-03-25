/**
 * An example that execute the module called MachineLearningBasedSentimentAnalysisModule over an example corpus in local/graphic mode.
 *
 * @author David Moreno Briz
 *
 */

package examples;

import java.io.File;

import gate.Corpus;
import gate.Factory;
import gate.Gate;
import gate.gui.MainFrame;
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
		MachineLearningBasedSentimentAnalysisModule module = new MachineLearningBasedSentimentAnalysisModule("Training");
		//Create the corpus and populate it.
	    //Corpus corpus = module.createCorpusAndPupulateItExample();
		Corpus corpus = module.createCorpusAndPupulateItTraining();
	    module.setCorpus(corpus); // Set corpus into the controller. 
		module.execute();
	}
}
