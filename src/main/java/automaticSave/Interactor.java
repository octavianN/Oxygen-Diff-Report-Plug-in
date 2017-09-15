package automaticSave;


/**
 * The class that helps save the paths after closing the program and then helps loading them 
 * when the program is reopened. 
 * @author Dina_Andrei
 *
 */
public interface Interactor {
	
	// getter and setters for the labels
	public String getThirdLabelField() ;

	public void setThirdLabelField(String thirdLabelField) ;
	
	public String getFirstLabelField();
	
	public void setFirstLabelField(String firstLabelField);

	public String getSecondLabelField();

	public void setSecondLabelField(String secondLabelField);
	
	
	
}
