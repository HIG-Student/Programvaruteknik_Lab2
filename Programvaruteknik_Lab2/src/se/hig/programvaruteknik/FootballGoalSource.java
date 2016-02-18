package se.hig.programvaruteknik;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.owlike.genson.Genson;

/**
 * Getting football scores from
 * <a href="http://www.everysport.com">Everysport</a>
 */
public class FootballGoalSource implements DataSource
{
    private static String apiKey = "1769e0fdbeabd60f479b1dcaff03bf5c";
    private String name;

    private Map<LocalDate, Double> data;

    /**
     * Gets goals
     * 
     * @param name
     *            The name of this source
     * @param league
     *            The league to get data from
     * @param limit
     *            The max amount of data to get
     * @param homeTeam
     *            Get data about the home team, else get data about the visiting
     *            team
     * @throws DataSourceException
     *             If errors occurs
     */
    public FootballGoalSource(String name, int league, int limit, boolean homeTeam) throws DataSourceException
    {
	this(
		name,
		jsonToMap(
			HTTPFetcher.fetchData(String.format(
				"http://api.everysport.com/v1/events?apikey=%s&league=%d&limit=%d",
				apiKey,
				league,
				limit)),
			homeTeam));
    }

    private FootballGoalSource(String name, Map<LocalDate, Double> data) throws DataSourceException
    {
	this.name = name;
	this.data = Collections.unmodifiableMap(data);
    }

    @SuppressWarnings("unchecked")
    private static Map<LocalDate, Double> jsonToMap(String json, boolean homeTeam) throws DataSourceException
    {
	try
	{
	    return ((List<Map<String, Object>>) new Genson().deserialize(json, Map.class).get("events"))
		    .stream()
		    .collect(
			    Collectors.<Map<String, Object>, LocalDate, Double> toMap(
				    (event) -> LocalDate.parse(event.get("startDate").toString().substring(0, 10)),
				    (event) -> Double.parseDouble(
					    event.get((homeTeam ? "home" : "visiting") + "TeamScore").toString())));
	}
	catch (Exception e)
	{
	    throw new DataSourceException(e);
	}
    }

    /**
     * Create {@link FootballGoalSource} from json string
     * 
     * @param name
     *            The name of the new {@link FootballGoalSource}
     * @param json
     *            The json data
     * @param homeTeam
     *            Get data about the home team, else get data about the visiting
     *            team
     * @return The resulting {@link FootballGoalSource}
     * @throws DataSourceException
     *             If errors occurs
     */
    public static FootballGoalSource fromJSON(String name, String json, boolean homeTeam) throws DataSourceException
    {
	return new FootballGoalSource(name, jsonToMap(json, homeTeam));
    }

    @Override
    public String getName()
    {
	return name;
    }

    @Override
    public String getUnit()
    {
	return "Goals";
    }

    @Override
    public Map<LocalDate, Double> getData()
    {
	return data;
    }
}
