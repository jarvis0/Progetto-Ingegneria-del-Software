package it.polimi.ingsw.ps23.view;

import java.util.Observable;
import java.util.Observer;

public abstract class View extends Observable implements Runnable, Observer {

	//protected abstract void showModel(ModelView model);
	
	@Override
	public void update(Observable o, Object arg) {
		//modelView richiama questo metodo
	}
	
}