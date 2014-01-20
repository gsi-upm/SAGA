package gateModules;

import gate.Gate;
import gate.gui.MainFrame;
import pr.CountSentiment;

public class MainTest {
	
	/**
	 * Execute the module in GATE graphic mode.
	 * 
	 * @param args not used
	 * @throws Exception
	 */
	
	public static void main(String[] args) throws Exception{
		Gate.init(); // Prepare the library
		MainFrame.getInstance().setVisible(true); //Set GATE app visible
		ModuleWD module = new ModuleWD("Easy sentiment count");
		CountSentiment count = new ModuleCount().getCountTokens();
		module.add(count);
		module.execute();
	}
}
