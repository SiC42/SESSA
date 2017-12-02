package org.aksw.sessa.importing.dictionary.implementation;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.importing.dictionary.DictionaryInterface;
import org.aksw.sessa.importing.dictionary.FileBasedDictionaryInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a HashMap-based dictionary given a file handler.
 * This class is an implementation of the interface {@link DictionaryInterface}.
 *
 * @author Simon Bordewisch
 */
public class HashMapDictionary implements FileBasedDictionaryInterface {

  private static final Logger log = LoggerFactory.getLogger(FileBasedDictionaryInterface.class);
  private Map<String, Set<String>> dictionary;

  /**
   * Initializes the dictionary with given file handler.
   * The file will be parsed into the dictionary.
   * @param handler handler to be used for filling the dictionary
   */
  public HashMapDictionary(FileHandlerInterface handler) {
      dictionary = createDictionary(handler);
  }


  /**
   * Creates the dictionary based on every entry that the file handler gives
   * @param handler file handler that has file information
   * @return mapping of n-grams to set of URIs
   */
   protected Map<String, Set<String>> createDictionary(FileHandlerInterface handler) {
    Map<String, Set<String>> dictionary = new HashMap<>();
    try{
      for (Entry<String, String> entry; (entry = handler.nextEntry()) != null; ) {
        String key = entry.getKey();
        Set<String> values = dictionary.get(key);
        if (values == null) {
          values = new HashSet<>();
        }
        values.add(entry.getValue());
        dictionary.put(key, values);
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
    return dictionary;
  }

  /**
   * Given a n-gram, returns a set of URIs related to it or null if this map contains
   * no mapping for the key.
   * @param nGram n-gram whose associated value is to be returned
   * @return mapping of n-grams to set of URIs
   */
  @Override
  public Set<String> get(String nGram) {
    return dictionary.get(nGram);
  }

  /**
   * Adds the entries in the give file to the dictionary.
   * @param handler handler that has file information
   */
  public void putAll(FileHandlerInterface handler){
    dictionary.putAll(createDictionary(handler));
  }

  /**
   * Returns a set view of the mappings contained in this map.
   * @return a set view of the mappings contained in this map
   */
  public Set<Entry<String, Set<String>>> entrySet() {
    return dictionary.entrySet();
  }
}
