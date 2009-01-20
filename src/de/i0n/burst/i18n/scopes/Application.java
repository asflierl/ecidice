package de.i0n.burst.i18n.scopes;

import de.i0n.burst.i18n.L10nEnum;
import de.i0n.burst.i18n.Scope;

public enum Application implements L10nEnum {
    AppName,
    WindowTitle;
    
    public Scope getScope() { return Scope.Application; }
}
