/**
 * An example that execute the module called DictionaryBasedSentimentAnalyzer over an example corpus in local/graphic mode.
 *
 * @author David Moreno Briz
 *
 */

package examples;

import java.io.File;

import gate.Corpus;
import gate.Gate;
import gate.gui.MainFrame;
import gateModules.DictionaryBasedSentimentAnalyzer;

public class FinancialSentimentAnalysis{
	
	/**
	 * Execute "DictionaryBasedSentimentAnalyzer" module in GATE graphic/local mode.
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
		// Load the plugin
		Gate.getCreoleRegister().registerDirectories(aPluginDir.toURI().toURL());
		//Create DictionaryBasedSentimentAnalyzer and set the gazzeetter that we are goingt to use in this example, which is about finances.
		DictionaryBasedSentimentAnalyzer module = new DictionaryBasedSentimentAnalyzer("saga - Financial Sentiment Analyzer", (new FinancialSentimentAnalysis()).getClass().getResource("/resources/gazetteer/finances/lists.def"));
		//Register our own plugins to use our own PRs located in the package processingResources.
		module.registerPrPlugin();
		//Create the corpus and populate it.
	    Corpus corpus = module.createCorpusAndPupulateItExample();
	    module.setCorpus(corpus); // Set corpus into the controller. 
		module.execute();
	}
}