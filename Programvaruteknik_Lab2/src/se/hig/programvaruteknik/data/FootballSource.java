package se.hig.programvaruteknik.data;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Getting football scores from
 * <a href="http://www.everysport.com">Everysport</a>
 */
public class FootballSource extends JSONSource
{
    private static String apiKey = "1769e0fdbeabd60f479b1dcaff03bf5c";

    private static JSONTextExtractor UNIT = (root) -> "Goals";

    @SuppressWarnings("unchecked")
    private static JSONListExtractor LIST_EXTRACTOR = (root) -> (List<Map<String, Object>>) root.get("events");

    private static JSONDateExtractor DATE_EXTRACTOR = (entry) -> LocalDate
	    .parse(entry.get("startDate").toString().substring(0, 10));

    private static Supplier<String> getURLFetcher(String parameters)
    {
	return DataSupplierFactory.createURLFetcher(
		String.format("http://api.everysport.com/v1/events?apikey=%s&%s", apiKey, parameters));
    }

    /**
     * Extracts the sum of the goals made by both teams
     */
    public final static JSONValueExtractor TOTAL_GOALS_EXTRACTOR = (entry) -> Double.parseDouble(
	    entry.get("homeTeamScore").toString()) + Double.parseDouble(entry.get("visitingTeamScore").toString());

    /**
     * Extracts the spectators
     */
    @SuppressWarnings("unchecked")
    public final static JSONValueExtractor SPECTATORS_EXTRACTOR = (entry) -> Double
	    .parseDouble(((Map<String, Object>) entry.get("facts")).get("spectators").toString());

    /**
     * Get goals from <a href="www.everysport.com">Everysport</a>
     * 
     * @param name
     *            The name of the source
     * @param valueExtractor
     *            The extractor that will extract the value (see
     *            {@link FootballSource#TOTAL_GOALS_EXTRACTOR})
     * @param parameters
     *            The <a href=
     *            "https://github.com/menmo/everysport-api-documentation/blob/master/endpoints/GET_events.md">
     *            parameters</a> to pass<br>
     *            "http://api.everysport.com/v1/events?apikey=<...>&{parameters}"
     * @throws DataSourceException
     *             If error occurs
     */
    public FootballSource(String name, JSONValueExtractor valueExtractor, String parameters) throws DataSourceException
    {
	this(name, valueExtractor, getURLFetcher(parameters));

	// league=%d&limit=%d
    }

    /**
     * Get goals from <a href="www.everysport.com">Everysport</a>
     * 
     * @param name
     *            The name of the source
     * @param valueExtractor
     *            The extractor that will extract the value (see
     *            {@link FootballSource#TOTAL_GOALS_EXTRACTOR})
     * @param file
     *            The JSON file to read
     * @throws DataSourceException
     *             If error occurs
     */
    public FootballSource(String name, JSONValueExtractor valueExtractor, File file) throws DataSourceException
    {
	this(name, valueExtractor, DataSupplierFactory.createFileFetcher(file.getPath()));
    }

    /**
     * Get goals from <a href="www.everysport.com">Everysport</a>
     * 
     * @param name
     *            The name of the source
     * @param valueExtractor
     *            The extractor that will extract the value (see
     *            {@link FootballSource#TOTAL_GOALS_EXTRACTOR})
     * @param sourceSupplier
     *            The supplier that supplies the source
     * @throws DataSourceException
     *             If error occurs
     */
    public FootballSource(String name, JSONValueExtractor valueExtractor, Supplier<String> sourceSupplier) throws DataSourceException
    {
	super(sourceSupplier, (root) -> name, UNIT, LIST_EXTRACTOR, DATE_EXTRACTOR, valueExtractor);
    }
}
