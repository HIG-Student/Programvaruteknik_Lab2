package se.hig.programvaruteknik.data;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.owlike.genson.Genson;

import se.hig.programvaruteknik.model.DataCollection;
import se.hig.programvaruteknik.model.DataCollectionBuilder;
import se.hig.programvaruteknik.model.DataSource;
import se.hig.programvaruteknik.model.DataSource.DataSourceException;
import se.hig.programvaruteknik.model.MatchedDataPair;
import se.hig.programvaruteknik.model.MergeType;
import se.hig.programvaruteknik.model.Resolution;

/**
 * Builds rain sources
 */
public class RainSourceBuilder
{
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

    private Map<Period, String> periods = new TreeMap<>();

    /**
     * Creates a new builder
     * 
     * @param location
     *            The location to get data from
     */
    @SuppressWarnings("unchecked")
    public RainSourceBuilder(RainSourceLocation location)
    {
	for (Map<String, Object> period : (List<Map<String, Object>>) new Genson()
		.deserialize(DataSupplierFactory.createURLFetcher(location.url).get(), Map.class)
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
    }

    /**
     * Gets data from period
     * 
     * @param period
     *            The period to get from
     * @return The data
     * @throws RainSourceBuilderException
     *             If errors occur
     */
    @SuppressWarnings("unchecked")
    public DataSource getData(Period period) throws RainSourceBuilderException
    {
	try
	{
	    String csv_url = ((Map<String, List<Map<String, List<Map<String, String>>>>>) new Genson()
		    .deserialize(DataSupplierFactory.createURLFetcher(periods.get(period)).get(), Map.class))
			    .get("data")
			    .get(0)
			    .get("link")
			    .get(0)
			    .get("href");

	    String[] rows = DataSupplierFactory.createURLFetcher(csv_url).get().split("\\R+");

	    return new CSVDataSource(
		    DataSupplierFactory.createURLFetcher(csv_url),
		    (source) -> rows[1].split(";")[0],
		    (source) -> rows[3].split(";")[2],
		    (source) -> Arrays.asList(Arrays.copyOfRange(rows, 8, rows.length)),
		    (row, adder) -> adder
			    .accept(LocalDate.parse(row.split(";")[2]), Double.parseDouble(row.split(";")[3])));

	}
	catch (Exception e)
	{
	    throw new RainSourceBuilderException(e);
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

    /**
     * Indicates errors when building a rain source
     */
    @SuppressWarnings("serial")
    public class RainSourceBuilderException extends DataSourceException
    {
	/**
	 * Create exception
	 * 
	 * @param exception
	 *            The exception that resulted in this exception
	 */
	public RainSourceBuilderException(Exception exception)
	{
	    super(exception);
	}
    }

    public static void main(String[] args) throws DataSourceException
    {
	DataCollectionBuilder builder = new DataCollectionBuilder(
		new FootballSource(
			"Football Goals",
			FootballSource.TOTAL_GOALS_EXTRACTOR,
			new File("test/se/hig/programvaruteknik/data/TestEverysportData.json")),
		new RainSourceBuilder(RainSourceLocation.GÄVLE_A).getData(Period.OLD),
		Resolution.MONTH);

	builder.setYMergeType(MergeType.AVERAGE);

	System.out.println(builder.getTitle());
	DataCollection collection = builder.getResult();

	for (Entry<String, MatchedDataPair> pair : collection.getData().entrySet())
	{
	    System.out.println(pair);
	}
    }
}
