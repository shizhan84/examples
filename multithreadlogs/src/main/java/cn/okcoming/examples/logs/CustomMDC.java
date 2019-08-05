package cn.okcoming.examples.logs;
import org.slf4j.helpers.BasicMDCAdapter;
import org.slf4j.spi.MDCAdapter;

import java.util.Map;

public class CustomMDC {
    static MDCAdapter mdcAdapter;
    static {
         mdcAdapter = new BasicMDCAdapter();
    }

    /**
     * Put a diagnostic context value (the <code>val</code> parameter) as identified with the
     * <code>key</code> parameter into the current thread's diagnostic context map. The
     * <code>key</code> parameter cannot be null. The <code>val</code> parameter
     * can be null only if the underlying implementation supports it.
     * 
     * <p>
     * This method delegates all work to the MDC of the underlying logging system.
     *
     * @param key non-null key 
     * @param val value to put in the map
     * 
     * @throws IllegalArgumentException
     *           in case the "key" parameter is null
     */
    public static void put(String key, String val) throws IllegalArgumentException {

        mdcAdapter.put(key, val);
    }


    public static String get(String key) throws IllegalArgumentException {


        return mdcAdapter.get(key);
    }

    /**
     * Remove the diagnostic context identified by the <code>key</code> parameter using
     * the underlying system's MDC implementation. The <code>key</code> parameter
     * cannot be null. This method does nothing if there is no previous value
     * associated with <code>key</code>.
     *
     * @param key  
     * @throws IllegalArgumentException
     *           in case the "key" parameter is null
     */
    public static void remove(String key) throws IllegalArgumentException {

        mdcAdapter.remove(key);
    }

    /**
     * Clear all entries in the MDC of the underlying implementation.
     */
    public static void clear() {

        mdcAdapter.clear();
    }

    /**
     * Return a copy of the current thread's context map, with keys and values of
     * type String. Returned value may be null.
     * 
     * @return A copy of the current thread's context map. May be null.
     * @since 1.5.1
     */
    public static Map<String, String> getCopyOfContextMap() {

        return mdcAdapter.getCopyOfContextMap();
    }

    /**
     * Set the current thread's context map by first clearing any existing map and
     * then copying the map passed as parameter. The context map passed as
     * parameter must only contain keys and values of type String.
     * 
     * @param contextMap
     *          must contain only keys and values of type String
     * @since 1.5.1
     */
    public static void setContextMap(Map<String, String> contextMap) {

        mdcAdapter.setContextMap(contextMap);
    }

    /**
     * Returns the MDCAdapter instance currently in use.
     * 
     * @return the MDcAdapter instance currently in use.
     * @since 1.4.2
     */
    public static MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

}
