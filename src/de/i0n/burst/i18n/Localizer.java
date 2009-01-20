package de.i0n.burst.i18n;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public final class Localizer {
    private static final String SCOPES_PACKAGE_NAME_FORMAT = "%s.scopes.%s";
    private static final Localizer instance = new Localizer();
    
    private final Map<Scope, ScopeResource<?>> bundles =
        new EnumMap<Scope, ScopeResource<?>>(Scope.class);
    
    private Locale locale;
    
    private Localizer() {
        reset(Locale.ENGLISH);
    }
    
    @SuppressWarnings("unchecked") // type safety ensured at runtime
    private void reset(Locale loc) {
        bundles.clear();
        locale = loc;
        
        for (Scope scope : Scope.values()) {
            final Class<?> type;
            try {
                type = Class.forName(String.format(
                        SCOPES_PACKAGE_NAME_FORMAT, 
                        getClass().getPackage().getName(),
                        scope.name()));
            } catch (ClassNotFoundException exc) {
                throw new AssertionError("scope '" + scope 
                        + "' should have a corresponding class");
            }
                    
            
            assert type.isEnum() : "type should be an enum";
            assert L10nEnum.class.isAssignableFrom(type) 
                : "type should implement L10nEnum";
            
            put(scope, type.asSubclass(Enum.class));
        }
    }
    
    private <T extends Enum<T>> void put(Scope scope, Class<T> type) {
        assert !bundles.containsKey(scope) : "scope should only be used once";
        
        bundles.put(scope, new ScopeResource<T>(type, locale));
    }
    
    public static void setLocale(Locale locale) {
        Localizer.instance.reset(locale);
    }
    
    public static <T extends Enum<T> & L10nEnum> String get(T key) {
        return instance.bundles.get(key.getScope()).get(key);
    }
}
