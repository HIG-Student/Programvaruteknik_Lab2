package se.hig.programvaruteknik;

/**
 * Clever utilities
 */
public class Utils
{
    /**
     * Returns the first non-null parameter
     * 
     * @param options
     *            Options
     * @return The first non-null option
     */
    @SafeVarargs
    public static <T> T or(T... options)
    {
	if (options == null) return null;

	for (T option : options)
	{
	    if (option != null) return option;
	}

	return null;
    }
}
