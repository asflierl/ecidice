package de.i0n.burst.i18n;

/**
 * Marker interface to group together localization enum types.
 * 
 * @author i0n
 */
public interface L10nEnum { 
    /**
     * Returns the scope of this type of l10n constants.
     * 
     * @return the scope for a marked enum
     */
    Scope getScope();
}
