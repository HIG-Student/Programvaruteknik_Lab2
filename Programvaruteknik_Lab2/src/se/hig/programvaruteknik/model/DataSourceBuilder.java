package se.hig.programvaruteknik.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import java.util.TreeMap;

/**
 * Generic builder of datasources
 */
public abstract class DataSourceBuilder
{
    private BiFunction<LocalDate, Double, Boolean> dataFilter;

    /**
     * The name of the datasource
     */
    public final CachedValue<String> name = new CachedValue<>();

    /**
     * Sets the data filter
     * 
     * @param dataFilter
     *            The data filter
     * @return This builder
     */
    public DataSourceBuilder setDataFilter(BiFunction<LocalDate, Double, Boolean> dataFilter)
    {
	this.dataFilter = dataFilter;
	return this;
    }

    /**
     * Sets the name of the datasource
     * 
     * @param name
     *            The name
     * @return This builder
     */
    public DataSourceBuilder setName(String name)
    {
	this.name.updateSupplier(() -> name);
	return this;
    }

    /**
     * The unit of the datasource
     */
    public final CachedValue<String> unit = new CachedValue<>();

    /**
     * Sets the unit of the datasource
     * 
     * @param unit
     *            The unit
     * @return This builder
     */
    public DataSourceBuilder setUnit(String unit)
    {
	this.unit.updateSupplier(() -> unit);
	return this;
    }

    protected abstract Map<LocalDate, Double> generateData();

    /**
     * Builds the data source
     * 
     * @return The data source
     * @throws DataSourceBuilderException
     *             If errors occurs
     */
    public final DataSource build()
    {
	return new DataSource()
	{
	    private Map<LocalDate, Double> data;

	    private String name = null;
	    private String unit = null;

	    {
		try
		{
		    Map<LocalDate, Double> generatedData = generateData();
		    if (generatedData == null) throw new DataSourceBuilderException("Missing data");
		    if (!DataSourceBuilder.this.name
			    .canGiveValue()) throw new DataSourceBuilderException("Missing name");
		    if (!DataSourceBuilder.this.unit
			    .canGiveValue()) throw new DataSourceBuilderException("Missing unit");

		    name = DataSourceBuilder.this.name.get();
		    unit = DataSourceBuilder.this.unit.get();

		    Map<LocalDate, Double> rawData = new TreeMap<>();
		    for (Entry<LocalDate, Double> entry : generatedData.entrySet())
		    {
			if (dataFilter == null || !dataFilter.apply(entry.getKey(), entry.getValue()))
			{
			    rawData.put(entry.getKey(), entry.getValue());
			}
		    }

		    data = Collections.unmodifiableMap(rawData);
		}
		catch (Exception exception)
		{
		    throw (DataSourceBuilderException) (exception instanceof DataSourceBuilderException ? exception : new DataSourceBuilderException(
			    exception));
		}
	    }

	    @Override
	    public String getUnit()
	    {
		return unit;
	    }

	    @Override
	    public String getName()
	    {
		return name;
	    }

	    @Override
	    public Map<LocalDate, Double> getData()
	    {
		return data;
	    }
	};
    }

    /**
     * Indicates errors when building a rain source
     */
    @SuppressWarnings("serial")
    public class DataSourceBuilderException extends RuntimeException
    {
	/**
	 * Create exception
	 * 
	 * @param exception
	 *            The exception that resulted in this exception
	 */
	public DataSourceBuilderException(Exception exception)
	{
	    super(exception);
	}

	/**
	 * Create exception
	 * 
	 * @param exception
	 *            The reason
	 */
	public DataSourceBuilderException(String exception)
	{
	    super(exception);
	}
    }
}
