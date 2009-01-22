package de.i0n.burst.i18n;

import java.util.*;

import de.i0n.concurrent.Immutable;

/**
 * Works like a {@link ResourceBundle} for receiving translated strings, but
 * uses enum keys instead of strings as keys for faster access times.
 * 
 * @author i0n
 *
 * @param <T> the l10n enum type
 */
@Immutable
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
        
        final Set<String> bundleKeys = 
            new HashSet<String>(Collections.list(bundle.getKeys()));
        
        final Set<T> enumKeys = EnumSet.allOf(type);
        
        assert bundleKeys.equals(enumKeys) : 
            "enum keys should match the ones in the associated bundle";
        
        for (T key : enumKeys) {
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
