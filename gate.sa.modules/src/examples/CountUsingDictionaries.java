/**
 * An example that execute the module called ModuleCount over an example corpus in local/graphic mode.
 *
 * @author David Moreno Briz
 *
 */

package examples;

import java.io.File;

import gate.Corpus;
import gate.Gate;
import gate.gui.MainFrame;
import gateModules.ModuleCount;

public class CountUsingDictionaries{
	
	/**
	 * Execute "ModuleCount" module in GATE graphic/local mode.
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
		//Create ModuleCount
		ModuleCount module = new ModuleCount("Easy sentiment count");
		//Register our own plugins to use our own PRs located in the package pr.
		module.registerPrPlugin();
		//Create the corpus and populate it.
	    Corpus corpus = module.createCorpusAndPupulateItExample();
	    module.setCorpus(corpus); // Set corpus into the controller. 
		module.execute();
	}
}
