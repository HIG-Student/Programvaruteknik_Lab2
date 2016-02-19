package se.hig.programvaruteknik.data;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Getting football scores from
 * <a href="http://www.everysport.com">Everysport</a>
 */
public class FootballGoalSource extends JSONSource
{
    private static String apiKey = "1769e0fdbeabd60f479b1dcaff03bf5c";

    private static JSONTextExtractor UNIT = (root) -> "Goals";

    @SuppressWarnings("unchecked")
    private static JSONListExtractor LIST_EXTRACTOR = (root) -> (List<Map<String, Object>>) root.get("events");

    private static JSONDateExtractor DATE_EXTRACTOR = (entry) -> LocalDate
	    .parse(entry.get("startDate").toString().substring(0, 10));

    private static Function<Boolean, JSONValueExtractor> GOALS_EXTRACTOR = (homeTeam) -> (entry) -> Double
	    .parseDouble(entry.get((homeTeam ? "home" : "visiting") + "TeamScore").toString()));

    /**
     * Get goals from <a href="www.everysport.com">Everysport</a>
     * 
     * @param name
     *            The name of the source
     * @param homeTeam
     *            If the hometeam's goals should be chosen, else the
     *            visitingteam's is chosen
     * @param parameters
     *            The parameters to pass
     *            "http://api.everysport.com/v1/events?apikey=<...>&{parameters}"
     * @throws DataSourceException
     *             If error occurs
     */
    public FootballGoalSource(String name, boolean homeTeam, String parameters) throws DataSourceException
    {
	super(
		() -> DataFetcher.fetchDataFromURL(
			String.format("http://api.everysport.com/v1/events?apikey=%s&%s", apiKey, parameters)),
		(root) -> name,
		UNIT,
		LIST_EXTRACTOR,
		DATE_EXTRACTOR,
		GOALS_EXTRACTOR.apply(homeTeam));

	// league=%d&limit=%d
    }

    /**
     * Get goals from <a href="www.everysport.com">Everysport</a>
     * 
     * @param name
     *            The name of the source
     * @param homeTeam
     *            If the hometeam's goals should be chosen, else the
     *            visitingteam's is chosen
     * @param file
     *            The JSON file to read
     * @throws DataSourceException
     *             If error occurs
     */
    public FootballGoalSource(String name, boolean homeTeam, File file) throws DataSourceException
    {
	super(
		() -> DataFetcher.fetchDataFromFile(file.getPath()),
		(root) -> name,
		UNIT,
		LIST_EXTRACTOR,
		DATE_EXTRACTOR,
		GOALS_EXTRACTOR.apply(homeTeam));

    }

    private FootballGoalSource(String name, boolean homeTeam, Map<String, Object> data) throws DataSourceException
    {
	super(data, (root) -> name, UNIT, LIST_EXTRACTOR, DATE_EXTRACTOR, GOALS_EXTRACTOR.apply(homeTeam));
    }
}
