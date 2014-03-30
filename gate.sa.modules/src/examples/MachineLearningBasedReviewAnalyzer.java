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
import gate.Gate;
import gate.gui.MainFrame;
import gate.learning.RunMode;
import gateModules.MachineLearningBasedSentimentAnalysisModule;

public class MachineLearningBasedReviewAnalyzer{
	
	/**
	 * Execute "MachineLearningBasedSentimentAnalysisModule" module in GATE graphic/local mode.
	 * 
	 * @param args not used
	 * @throws Exception
	 */
	
	public static void main(String[] args) throws Exception{
		Gate.init(); // Prepare the library
		MainFrame.getInstance().setVisible(true); //Set GATE app visible
		
		// Get the root plugins dir
		File pluginsDir = Gate.getPluginsHome();
		
		// Load the Annie, Tools and Laerning plugins
		File aPluginDir = new File(pluginsDir, "ANNIE");
		File aPluginDir2 = new File(pluginsDir, "Tools");
		File aPluginDir3 = new File(pluginsDir, "Learning");
		
		// Register the plugins
		Gate.getCreoleRegister().registerDirectories(aPluginDir.toURI().toURL());
		Gate.getCreoleRegister().registerDirectories(aPluginDir2.toURI().toURL());
		Gate.getCreoleRegister().registerDirectories(aPluginDir3.toURI().toURL());
		
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("comment");
		
		//Training module
		MachineLearningBasedSentimentAnalysisModule trainingModule = new MachineLearningBasedSentimentAnalysisModule("Training","/resources/machineLearning/reviews/paum.xml", RunMode.TRAINING, "", list);
		//Create the corpus and populate it
		Corpus corpus = trainingModule.createCorpusAndPupulateIt("Training", "/resources/machineLearning/reviews/corpora/training");
		trainingModule.setCorpus(corpus); // Set corpus into the controller. 
		trainingModule.execute();
		
		//Application module
		MachineLearningBasedSentimentAnalysisModule applicationModule = new MachineLearningBasedSentimentAnalysisModule("Testing","/resources/machineLearning/reviews/paum.xml", RunMode.APPLICATION, "output", list);
		//Create the corpus and populate it.
		Corpus corpus2 = applicationModule.createCorpusAndPupulateIt("Testing", "/resources/machineLearning/reviews/corpora/testing");
		applicationModule.setCorpus(corpus2); // Set corpus into the controller. 
		applicationModule.execute();
		
		//Evaluation module
		MachineLearningBasedSentimentAnalysisModule evaluationModule = new MachineLearningBasedSentimentAnalysisModule("Cross-validation","/resources/machineLearning/reviews/paum.xml", RunMode.EVALUATION, "", list);
		//Create the corpus and populate it.
		Corpus corpus3 = evaluationModule.createCorpusAndPupulateIt("All", "/resources/machineLearning/reviews/corpora/all");
		evaluationModule.setCorpus(corpus3); // Set corpus into the controller. 
		evaluationModule.execute();
				
	}
}
