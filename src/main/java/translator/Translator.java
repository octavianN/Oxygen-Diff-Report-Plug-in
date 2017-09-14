package translator;

/**
 * Interface used for internationalization.
 * 
 * @author Dina_Andrei
 *
 */
public interface Translator {

	/**
	 * Get the translation from the given key;
	 * 
	 * @param key
	 *          - the key.
	 * @return the translation.
	 */
	public String getTraslation(String key);
}