package examples;

import java.io.File;

import gate.Corpus;
import gate.Gate;
import gate.gui.MainFrame;
import gateModules.ModuleCount;
import gateModules.ModuleWD;
import pr.CountSentiment;

public class CountUsingDictionaries{
	
	/**
	 * Execute the module in GATE graphic/local mode with the module we want.
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
		// Let's load the Annie plugin
		File aPluginDir = new File(pluginsDir, "ANNIE");
		// Load the plugin.
		Gate.getCreoleRegister().registerDirectories(aPluginDir.toURI().toURL());
		ModuleWD module = new ModuleWD("Easy sentiment count");
		//Register our own plugin to use our own PRs located in the package pr.
		module.registerPrPlugin();
		//Create the PR we want to add to ModuleWD module.
		//Create the corpus and populate it.
	    Corpus corpus = module.createCorpusAndPupulateIt();
	    module.setCorpus(corpus); // Set corpus into the controller.
	    
		CountSentiment count = ModuleCount.getCountTokens();
		module.add(count); 
		module.execute();
	}
}
