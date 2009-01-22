package de.i0n.burst.i18n;

import java.util.*;

/**
 * Works like a {@link ResourceBundle} for receiving translated strings, but
 * uses enum keys instead of strings as keys for faster access times.
 * 
 * @author i0n
 *
 * @param <T> the l10n enum type
 */
class ScopeResource <T extends Enum<T>> {
    private Map<?, String> strings;
    
    /**
     * Creates the resource bundle from the properties file associated with the
     * specified l10n enum type, reads it's values and stores the enum key to
     * value associations in an internal map.
     * 
     * @param type the class object of the l10n enum type
     * @param locale the locale to use
     */
    ScopeResource(Class<T> type, Locale locale) {
        EnumMap<T, String> map = new EnumMap<T, String>(type);
        
        final ResourceBundle bundle = ResourceBundle.getBundle(type.getName(), 
                locale);
        
        final Set<String> rscKeys = 
            new HashSet<String>(Collections.list(bundle.getKeys()));
        
        for (T key : type.getEnumConstants()) {
            assert rscKeys.contains(key.name());
            map.put(key, bundle.getString(key.name()));
        }
        
        strings = map;
    }
    
    /**
     * Returns a localized string associated with the supplied key.
     * 
     * @param key a key identifying a localizable string resource
     * @return a localized string
     */
    public String get(L10nEnum key) {
        return strings.get(key);
    }
}
