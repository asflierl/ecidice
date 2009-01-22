package de.i0n.burst.i18n.scopes;

import de.i0n.burst.i18n.L10nEnum;
import de.i0n.burst.i18n.Localizer;
import de.i0n.burst.i18n.Scope;

/**
 * L10n constants of application scope.
 * 
 * @see Localizer
 * 
 * @author i0n
 */
public enum Application implements L10nEnum {
    AppName,
    WindowTitle;
    
    /* (non-Javadoc)
     * @see de.i0n.burst.i18n.L10nEnum#getScope()
     */
    public Scope getScope() { return Scope.Application; }
}
