package translator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class TranslatorImplementation implements Translator {

		public String getTraslation(String key) {
			return ((StandalonePluginWorkspace) PluginWorkspaceProvider.getPluginWorkspace()).getResourceBundle()
					.getMessage(key);
		}

	
}
