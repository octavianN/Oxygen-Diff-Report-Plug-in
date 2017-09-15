package automaticSave;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Implementation for the ContentPersiser Interface. Saves the paths in the HTML Diff Dialog.
 * @author Dina_Andrei
 *
 */
public class ContentPersisterIMPL implements ContentPersister{

	public ContentPersisterIMPL() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void savePath(Interactor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		
		//save the first file.
		optionsStorage.setOption(OptionKeys.HTML_DIFF_FIRST_FILE, String.valueOf(interactor.getFirstLabelField()));
		
		//save the second file.
		optionsStorage.setOption(OptionKeys.HTML_DIFF_SECOND_FILE, String.valueOf(interactor.getSecondLabelField()));
		
		//save th output file.
		optionsStorage.setOption(OptionKeys.HTML_DIFF_OUTPUT_FILE, String.valueOf(interactor.getThirdLabelField()));
	}

	@Override
	public void loadPath(Interactor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		
		String pathName;

		// set path for first file.
		pathName = optionsStorage.getOption(OptionKeys.HTML_DIFF_FIRST_FILE, "");
		if(!pathName.isEmpty()) {
			interactor.setFirstLabelField(pathName);
		}
		
		// set path for second file.
		pathName = optionsStorage.getOption(OptionKeys.HTML_DIFF_SECOND_FILE, "");
		if(!pathName.isEmpty()) {
			interactor.setSecondLabelField(pathName);
		}
		
		//set path for output file.
		pathName = optionsStorage.getOption(OptionKeys.HTML_DIFF_OUTPUT_FILE, "");
		if(!pathName.isEmpty()) {
			interactor.setThirdLabelField(pathName);
		}
		
	}
	
	
	
}
