package de.i0n.burst.i18n;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

class ScopeResource <T extends Enum<T>> {
    private Map<?, String> strings;
    
    ScopeResource(Class<T> type, Locale locale) {
        EnumMap<T, String> map = new EnumMap<T, String>(type);
        
        final ResourceBundle bundle = ResourceBundle.getBundle(type.getName(), 
                locale);
        
        for (T key : type.getEnumConstants()) {
            map.put(key, bundle.getString(key.name()));
        }
        
        strings = map;
    }
    
    public String get(L10nEnum key) {
        return strings.get(key);
    }
}
