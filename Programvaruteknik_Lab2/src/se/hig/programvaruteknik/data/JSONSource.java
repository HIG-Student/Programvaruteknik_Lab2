package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import com.owlike.genson.Genson;

import se.hig.programvaruteknik.model.DataSource;

/**
 * A source of JSON data
 */
public class JSONSource implements DataSource
{
    private String name;
    private String unit;
    private Map<LocalDate, Double> data = new TreeMap<>();

    /**
     * Creates a JSON datasource form a webresource
     * 
     * @param sourceSupplier
     *            The supplier of the JSON source
     * @param nameExtractor
     *            The {@link JSONSource.JSONTextExtractor extractor} to get the
     *            name with
     * @param unitExtractor
     *            The {@link JSONSource.JSONTextExtractor extractor} to get the
     *            unit with
     * @param listExtractor
     *            The {@link JSONSource.JSONListExtractor extractor} to get the
     *            list with
     * @param dateExtractor
     *            The {@link JSONSource.JSONDateExtractor extractor} to get the
     *            date from a list entry with
     * @param valueExtractor
     *            The {@link JSONSource.JSONValueExtractor extractor} to get the
     *            value from a list entry with
     * @throws DataSourceException
     *             If errors occurs
     */
    @SuppressWarnings("unchecked")
    public JSONSource(Supplier<String> sourceSupplier, JSONTextExtractor nameExtractor, JSONTextExtractor unitExtractor, JSONListExtractor listExtractor, JSONDateExtractor dateExtractor, JSONValueExtractor valueExtractor) throws DataSourceException
    {
	try
	{
	    extractFromRoot(
		    new Genson().deserialize(sourceSupplier.get(), Map.class),
		    nameExtractor,
		    unitExtractor,
		    listExtractor,
		    dateExtractor,
		    valueExtractor);
	}
	catch (Exception e)
	{
	    throw new DataSourceException(e);
	}
    }

    /**
     * Creates a JSON datasource form a webresource
     * 
     * @param root
     *            The JSON root
     * @param nameExtractor
     *            The {@link JSONSource.JSONTextExtractor extractor} to get the
     *            name with
     * @param unitExtractor
     *            The {@link JSONSource.JSONTextExtractor extractor} to get the
     *            unit with
     * @param listExtractor
     *            The {@link JSONSource.JSONListExtractor extractor} to get the
     *            list with
     * @param dateExtractor
     *            The {@link JSONSource.JSONDateExtractor extractor} to get the
     *            date from a list entry with
     * @param valueExtractor
     *            The {@link JSONSource.JSONValueExtractor extractor} to get the
     *            value from a list entry with
     * @throws DataSourceException
     *             If errors occurs
     */
    public JSONSource(Map<String, Object> root, JSONTextExtractor nameExtractor, JSONTextExtractor unitExtractor, JSONListExtractor listExtractor, JSONDateExtractor dateExtractor, JSONValueExtractor valueExtractor) throws DataSourceException
    {
	try
	{
	    extractFromRoot(root, nameExtractor, unitExtractor, listExtractor, dateExtractor, valueExtractor);
	}
	catch (Exception e)
	{
	    throw new DataSourceException(e);
	}
    }

    private void extractFromRoot(Map<String, Object> root, JSONTextExtractor nameExtractor, JSONTextExtractor unitExtractor, JSONListExtractor listExtractor, JSONDateExtractor dateExtractor, JSONValueExtractor valueExtractor)
    {
	name = nameExtractor.extract(root);
	unit = unitExtractor.extract(root);

	for (Map<String, Object> map : listExtractor.extract(root))
	{
	    data.put(dateExtractor.extract(map), valueExtractor.extract(map));
	}
    }

    @Override
    public String getName()
    {
	return name;
    }

    @Override
    public String getUnit()
    {
	return unit;
    }

    @Override
    public Map<LocalDate, Double> getData()
    {
	return Collections.unmodifiableMap(data);
    }

    /**
     * Extractor to extract a string from a JSON map
     * 
     * @see Genson#deserialize(String, Map)
     */
    public interface JSONTextExtractor
    {
	/**
	 * Extract the text from the map
	 * 
	 * @param root
	 *            The map to extract from
	 * @return The resulting text
	 */
	public String extract(Map<String, Object> root);
    }

    /**
     * Extractor to extract a list from a JSON map
     * 
     * @see Genson#deserialize(String, Map)
     */
    public interface JSONListExtractor
    {
	/**
	 * Extract the list from the map
	 * 
	 * @param root
	 *            The map to extract from
	 * @return The resulting list
	 */
	public List<Map<String, Object>> extract(Map<String, Object> root);
    }

    /**
     * Extractor to extract a date from a JSON map
     * 
     * @see Genson#deserialize(String, Map)
     */
    public interface JSONDateExtractor
    {
	/**
	 * Extract the date from the map-entry
	 * 
	 * @param root
	 *            The map-entry to extract from
	 * @return The resulting date
	 */
	public LocalDate extract(Map<String, Object> root);
    }

    /**
     * Extractor to extract a value from a JSON map
     * 
     * @see Genson#deserialize(String, Map)
     */
    public interface JSONValueExtractor
    {
	/**
	 * Extract the value from the map-entry
	 * 
	 * @param root
	 *            The map-entry to extract from
	 * @return The resulting value
	 */
	public Double extract(Map<String, Object> root);
    }
}
