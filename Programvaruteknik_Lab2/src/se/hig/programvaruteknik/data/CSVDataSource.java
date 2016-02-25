package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import se.hig.programvaruteknik.model.DataSource;

/**
 * A source of CSV data
 */
public class CSVDataSource implements DataSource
{
    private String name;
    private String unit;
    private Map<LocalDate, Double> data = new TreeMap<>();

    /**
     * Splits the source based on line-breaks (regex '\R')
     */
    public static Function<String, List<String>> DEFAULT_ROWEXTRACTOR = (source) -> Arrays.asList(source.split("\\R"));

    /**
     * Adds the value to the map
     */
    private final BiConsumer<LocalDate, Double> ADDER = (date, value) -> data.put(date, value);

    /**
     * CSV data source
     * 
     * @param sourceSupplier
     *            Suplier for the source
     * @param nameExtractor
     *            Extractor that extract the name from the source
     * @param unitExtractor
     *            Extractor that extract the unit from the source
     * @param rowExtractor
     *            Extractor that extract the rows from the source (see
     *            {@link CSVDataSource#DEFAULT_ROWEXTRACTOR})
     * @param dataExtractor
     *            Extractor that extract the data from the row, and can add it
     *            with the adder
     */
    public CSVDataSource(Supplier<String> sourceSupplier, Function<String, String> nameExtractor, Function<String, String> unitExtractor, Function<String, List<String>> rowExtractor, BiConsumer<String, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	String source = sourceSupplier.get();

	name = nameExtractor.apply(source);
	unit = unitExtractor.apply(source);

	for (String row : rowExtractor.apply(source))
	{
	    dataExtractor.accept(row, ADDER);
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
}
