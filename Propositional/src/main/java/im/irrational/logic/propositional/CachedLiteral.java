package im.irrational.logic.propositional;

import java.util.HashMap;

/***
 * Literal but with global caching
 */
public class CachedLiteral extends Literal {
    public static CachedLiteral TRUE = new CachedLiteral("TRUE", true);
    public static CachedLiteral FALSE = new CachedLiteral("FALSE", false);

    private static final HashMap<String, CachedLiteral> globalCache = new HashMap<>();

    static {
        globalCache.put(TRUE.getDisplayName(), TRUE);
        globalCache.put(NEGATION_SYMBOL.concat(TRUE.getDisplayName()), FALSE);
        globalCache.put(FALSE.getDisplayName(), FALSE);
        globalCache.put(NEGATION_SYMBOL.concat(FALSE.getDisplayName()), TRUE);
    }

    private CachedLiteral(final String displayName, final boolean value) {
        super(displayName, value);
    }

    public static CachedLiteral newInstance(final String displayName, final boolean value) {
        String key = value ? displayName : NEGATION_SYMBOL.concat(displayName);
        return globalCache.computeIfAbsent(key, k -> new CachedLiteral(displayName, value));
    }

    public static CachedLiteral of(final String displayName, final boolean value) {
        return newInstance(displayName, value);
    }
}
