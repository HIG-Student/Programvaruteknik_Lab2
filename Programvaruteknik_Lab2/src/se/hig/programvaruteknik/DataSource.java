package se.hig.programvaruteknik;

import java.time.LocalDate;
import java.util.Map;

/**
 * Interface representing an source of data
 */
public interface DataSource
{
    /**
     * Get the name of the data source
     * 
     * @return The name
     */
    public String getName();

    /**
     * Get the unit of the data source
     * 
     * @return The unit
     */
    public String getUnit();

    /**
     * Get the data in the data source
     * 
     * @return The data
     */
    public Map<LocalDate, Double> getData();

    /**
     * Represent an error related to the datasource
     */
    @SuppressWarnings("serial")
    public class DataSourceException extends Exception
    {
	/**
	 * Create an exception
	 * 
	 * @param exception
	 *            The source exception
	 */
	public DataSourceException(Exception exception)
	{
	    super(exception);
	}
    }
}
