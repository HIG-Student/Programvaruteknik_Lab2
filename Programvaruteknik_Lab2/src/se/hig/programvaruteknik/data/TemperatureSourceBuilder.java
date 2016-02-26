package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.owlike.genson.Genson;

/**
 * Builds temperature sources
 */
public class TemperatureSourceBuilder extends CSVDataSourceBuilder
{
    private Map<Period, String> periods = new TreeMap<>();

    /**
     * Creates a new builder
     * 
     * @param location
     *            The location to get data from
     * 
     * @throws DataSourceBuilderException
     *             If errors occurs
     */
    public TemperatureSourceBuilder(TemperatureSourceLocation location)
    {
	this((url) -> DataSupplierFactory.createURLFetcher(url).get(), location);
    }

    /**
     * Creates a new builder
     * 
     * @param dataFetcher
     *            The fetcher that fetches data from the supplied url
     * 
     * @param location
     *            The location to get data from
     * 
     * @throws DataSourceBuilderException
     *             If errors occurs
     */
    @SuppressWarnings("unchecked")
    public TemperatureSourceBuilder(Function<String, String> dataFetcher, TemperatureSourceLocation location)
    {
	try
	{
	    for (Map<String, Object> period : (List<Map<String, Object>>) new Genson()
		    .deserialize(dataFetcher.apply(location.url), Map.class)
		    .get("period"))
	    {
		Period period_span = Period.fromKey((String) period.get("key"));
		if (period_span != null)
		{
		    for (Map<String, Object> link : (List<Map<String, Object>>) period.get("link"))
		    {
			if (link.get("type").equals("application/json"))
			{
			    periods.put(period_span, (String) link.get("href"));
			}
		    }
		}
	    }

	    setNameExtractor((source) -> source.split("\\R+")[1].split(";")[0]);
	    setUnitExtractor((source) -> source.split("\\R+")[3].split(";")[2]);
	    setRowExtractor((source) ->
	    {
		String[] rows = source.split("\\R+");
		return Arrays.asList(Arrays.copyOfRange(source.split("\\R+"), 7, rows.length));
	    });
	    setDataExtractor(
		    (row, adder) -> adder
			    .accept(LocalDate.parse(row.split(";")[2]), Double.parseDouble(row.split(";")[3])));
	}
	catch (

	Exception exception)

	{
	    throw (DataSourceBuilderException) (exception instanceof DataSourceBuilderException ? exception : new DataSourceBuilderException(
		    exception));
	}

    }

    /**
     * Get available periods
     * 
     * @return The available periods
     */
    public Period[] getPeriods()
    {
	return periods.keySet().toArray(new Period[0]);
    }

    /**
     * Check if the period is available
     * 
     * @param period
     *            The period to check
     * @return The result
     */
    public boolean isAvailable(Period period)
    {
	for (Period availible : getPeriods())
	    if (availible.equals(period)) return true;
	return false;
    }

    private Period period = null;

    /**
     * Sets the period
     * 
     * @param period
     *            The period to pick
     * 
     * @return This builder
     */
    public TemperatureSourceBuilder setPeriod(Period period)
    {
	return setPeriod((url) -> DataSupplierFactory.createURLFetcher(url).get(), period);
    }

    /**
     * Sets the period
     * 
     * @param dataFetcher
     *            The fetcher that fetches data from the supplied url
     * 
     * @param period
     *            The period to pick
     * 
     * @return This builder
     */
    public TemperatureSourceBuilder setPeriod(Function<String, String> dataFetcher, Period period)
    {
	if (!isAvailable(period)) throw new DataSourceBuilderException("Unavailable period");
	this.period = period;

	setSourceSupplier(() ->
	{
	    @SuppressWarnings("unchecked")
	    String csv_url = ((Map<String, List<Map<String, List<Map<String, String>>>>>) new Genson()
		    .deserialize(dataFetcher.apply(periods.get(this.period)), Map.class))
			    .get("data")
			    .get(0)
			    .get("link")
			    .get(0)
			    .get("href");

	    return dataFetcher.apply(csv_url);
	});

	return this;
    }

    /**
     * Specifies a period to get data from
     */
    public enum Period
    {
	/**
	 * The latest day
	 */
	LATEST("latest-day"),

	/**
	 * The latest four months
	 */
	FOUR_MONTHS("latest-months"),

	/**
	 * Older data
	 */
	OLD("corrected-archive");

	private String key;

	Period(String key)
	{
	    this.key = key;
	}

	/**
	 * Get period based on key
	 * 
	 * @param key
	 *            The key to get the period from
	 * @return The period
	 */
	public static Period fromKey(String key)
	{
	    switch (key)
	    {
	    case "latest-day":
	    case "latest-months":
		return null;
	    default:
		break;
	    }

	    for (Period p : Period.values())
		if (p.key.equals(key)) return p;

	    return null;
	}
    }

    /**
     * Filter to filter temperatures
     */
    @FunctionalInterface
    public interface TemperatureFilter
    {
	/**
	 * Filters temperatures
	 * 
	 * @param date
	 *            The date
	 * @param temperature
	 *            The temperature
	 * @return Whether it should be removed
	 */
	public boolean filter(LocalDate date, Double temperature);
    }

}
