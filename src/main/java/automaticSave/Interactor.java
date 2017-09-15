package automaticSave;

import java.util.Vector;

/**
 * The class that helps save the paths after closing the program and then helps loading them 
 * when the program is reopened. 
 * @author Dina_Andrei
 *
 */
public interface Interactor {
	
	// getter and setters for the labels
	public Vector<String> getThirdLabelField() ;

	public void setThirdLabelField(Vector<String> thirdLabelField) ;
	
	public Vector<String> getFirstLabelField();
	
	public void setFirstLabelField(Vector<String> firstLabelField);

	public Vector<String> getSecondLabelField();

	public void setSecondLabelField(Vector<String> secondLabelField);
	
	
	
}
