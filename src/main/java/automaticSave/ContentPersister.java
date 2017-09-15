package automaticSave;


/**
 * Used for saving and loading
 * @author Dina_Andrei
 *
 */
public interface ContentPersister {

	/**
	 * Save content before close the dialog.
	 * @param frame Checker interactor
	 */
	public void savePath(Interactor interactor);
	
	/**
	 * Load content before start the dialog.
	 * @param frame Checker interactor
	 */
	public void loadPath(Interactor interactor);
}