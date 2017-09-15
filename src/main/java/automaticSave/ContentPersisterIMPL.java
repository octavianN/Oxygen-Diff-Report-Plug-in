package automaticSave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;
import java.util.Vector;

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

	
	public static String join(String delimiter, Collection<String> resurces) {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence cs : resurces) {
			joiner.add(cs);
		}
		return joiner.toString();
	}
	
	@Override
	public void savePath(Interactor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		
		//save the first file.
		optionsStorage.setOption(OptionKeys.HTML_DIFF_FIRST_FILE, join(";", interactor.getFirstLabelField()));
		
		//save the second file.
		optionsStorage.setOption(OptionKeys.HTML_DIFF_SECOND_FILE, join(";",interactor.getSecondLabelField()));
		
		//save th output file.
		optionsStorage.setOption(OptionKeys.HTML_DIFF_OUTPUT_FILE, join(";",interactor.getThirdLabelField()));
	}

	
	@Override
	public void loadPath(Interactor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		
		String pathName;

		// set path for first file.
		pathName = optionsStorage.getOption(OptionKeys.HTML_DIFF_FIRST_FILE, "");
		if(!pathName.isEmpty()) {
			List<String> list = new ArrayList<String>(Arrays.asList(pathName.split(";")));
			HashSet<String> set = new HashSet<String>();
			set.addAll(list);
			Vector<String> pathNames = new Vector<>();
			pathNames.addAll(set);
			interactor.setFirstLabelField(pathNames);
		}
		
		// set path for second file.
		pathName = optionsStorage.getOption(OptionKeys.HTML_DIFF_SECOND_FILE, "");
		if(!pathName.isEmpty()) {
			List<String> list = new ArrayList<String>(Arrays.asList(pathName.split(";")));
			HashSet<String> set = new HashSet<String>();
			set.addAll(list);
			Vector<String> pathNames = new Vector<>();
			pathNames.addAll(set);
			interactor.setSecondLabelField(pathNames);
		}
		
		//set path for output file.
		pathName = optionsStorage.getOption(OptionKeys.HTML_DIFF_OUTPUT_FILE, "");
		if(!pathName.isEmpty()) {
			List<String> list = new ArrayList<String>(Arrays.asList(pathName.split(";")));
			HashSet<String> set = new HashSet<String>();
			set.addAll(list);
			Vector<String> pathNames = new Vector<>();
			pathNames.addAll(set);
			interactor.setThirdLabelField(pathNames);
		}
		
	}
	
	
	
}
