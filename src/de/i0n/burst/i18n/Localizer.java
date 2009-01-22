package de.i0n.burst.i18n;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import de.i0n.concurrent.GuardedBy;
import de.i0n.concurrent.ThreadSafe;

/**
 * Helper class for l10n that is based on enums as keys for the translated 
 * Strings. This helps type safety and execution speed.
 * <p>
 * There are different "scopes" (parts of the application) that match 1:1 to
 * different enums containing the actual keys. The name of such a scope enum
 * constant is exactly the name of the enum class that contains the keys for
 * that scope. Those classes reside in the <code>scopes</code> package. Each of
 * them has one <code>.properties</code> file per language in the same package.
 * In those files, each key entry must exactly match the name of an enum
 * constant in its associated enum class.
 * <p>
 * This class is final because its sole constructor is private.
 * 
 * @author i0n
 */
@ThreadSafe
public final class Localizer {
    private static final String SCOPES_PACKAGE_NAME_FORMAT = "%s.scopes.%s";
    private static final Localizer instance = new Localizer();
    
    @GuardedBy("instance") private final Map<Scope, ScopeResource<?>> bundles =
        new EnumMap<Scope, ScopeResource<?>>(Scope.class);
    
    @GuardedBy("instance") private Locale locale;
    
    /**
     * Creates a the sole instance of this class, setting English as initial
     * locale. This constructor is private because the class is a singleton.
     */
    private Localizer() {
        reset(Locale.ENGLISH);
    }
    
    /**
     * Clears all loaded bundles, sets the specified locale and (re-)loads all
     * bundles. For each scope constant a bundle is created. The keys of that
     * bundle are read and converted to a matching enum constant. That enum 
     * constant and the translated string are stored in a newly created
     * {@link ScopeResource} which in turn is then stored in {@link #bundles}. 
     * <p>
     * Contains some assertions to ensure type safety at runtime.
     * 
     * @param loc the new locale to set
     */
    @SuppressWarnings("unchecked") // type safety ensured at runtime
    private void reset(Locale loc) {
        synchronized (instance) {
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
    }
    
    /**
     * Creates a new {@link ScopeResource} for the supplied scope and enum type
     * and stores it in {@link #bundles}.
     * 
     * @param <T> the enum type associated with the scope
     * @param scope the scope to load and store
     * @param type the enum type's class
     */
    private <T extends Enum<T>> void put(Scope scope, Class<T> type) {
        assert !bundles.containsKey(scope) : "scope should only be used once";
        
        bundles.put(scope, new ScopeResource<T>(type, locale));
    }
    
    /**
     * Sets a new (global) locale.
     * 
     * @param locale the new locale
     */
    public static void setLocale(Locale locale) {
        instance.reset(locale);
    }
    
    /**
     * Gets the string associated with the specified key, translated to the
     * locale set by {@link #setLocale(Locale)} (default is 
     * {@link Locale#ENGLISH}).
     * 
     * @param <T> the enum type providing the specified l10n key
     * @param key the key specifying the string to translate
     * @return a localised string, associated with <code>key</code>
     */
    public static <T extends Enum<T> & L10nEnum> String get(T key) {
        synchronized (instance) {
            return instance.bundles.get(key.getScope()).get(key);
        }
    }
}
