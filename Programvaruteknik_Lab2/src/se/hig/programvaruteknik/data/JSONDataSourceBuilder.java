package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.owlike.genson.Genson;

import se.hig.programvaruteknik.model.CachedValue;
import se.hig.programvaruteknik.model.DataSourceBuilder;

/**
 * A source of JSON data
 */
public class JSONDataSourceBuilder extends DataSourceBuilder
{
    private CachedValue<String> source = new CachedValue<>();
    @SuppressWarnings("unchecked")
    private CachedValue<Map<String, Object>> root = new CachedValue<>(() ->
    {
	return new Genson().deserialize(source.get(), Map.class);
    });
    private CachedValue<List<Map<String, Object>>> list = new CachedValue<>();
    private CachedValue<Map<LocalDate, Double>> data = new CachedValue<>();

    private Function<Map<String, Object>, Boolean> entryFilter;
    private BiConsumer<Map<String, Object>, BiConsumer<LocalDate, Double>> dataExtractor;

    /**
     * Creates a datasource builder<br>
     * <br>
     * Components that needs to be added:
     * <ul>
     * <li>
     * {@link JSONDataSourceBuilder#setSourceSupplier(Supplier) SourceSupplier}
     * </li>
     * <li>
     * {@link JSONDataSourceBuilder#setNameExtractor(Function) NameExtractor} or
     * {@link JSONDataSourceBuilder#setName(String) Name}
     * </li>
     * <li>
     * {@link JSONDataSourceBuilder#setUnitExtractor(Function) UnitExtractor} or
     * {@link JSONDataSourceBuilder#setUnit(String) Unit}
     * </li>
     * <li>
     * {@link JSONDataSourceBuilder#setListExtractor(Function) ListExtractor}
     * </li>
     * <li>
     * {@link JSONDataSourceBuilder#setDataExtractor(BiConsumer) DataExtractor}
     * </li>
     * </ul>
     * Optional:
     * <ul>
     * <li>
     * {@link JSONDataSourceBuilder#setEntryFilter(Function) EntryFilter}
     * </li>
     * <li>
     * {@link JSONDataSourceBuilder#setDataFilter(BiFunction) DataFilter}
     * </li>
     * </ul>
     */
    public JSONDataSourceBuilder()
    {

    }

    /**
     * Creates a JSON datasource
     * 
     * @param sourceSupplier
     *            The supplier of the JSON source
     * @param nameExtractor
     *            The extractor to get the name with
     * @param unitExtractor
     *            The extractor to get the unit with
     * @param listExtractor
     *            The extractor to get the list with
     * @param dataExtractor
     *            The extractor to get the data from a list entry with
     */
    public JSONDataSourceBuilder(Supplier<String> sourceSupplier, Function<Map<String, Object>, String> nameExtractor, Function<Map<String, Object>, String> unitExtractor, Function<Map<String, Object>, List<Map<String, Object>>> listExtractor, BiConsumer<Map<String, Object>, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	setSourceSupplier(sourceSupplier);
	setNameExtractor(nameExtractor);
	setUnitExtractor(unitExtractor);
	setListExtractor(listExtractor);
	setDataExtractor(dataExtractor);
    }

    /**
     * Creates a JSON datasource
     * 
     * @param root
     *            The root element
     * @param nameExtractor
     *            The extractor to get the name with
     * @param unitExtractor
     *            The extractor to get the unit with
     * @param listExtractor
     *            The extractor to get the list with
     * @param dataExtractor
     *            The extractor to get the data from a list entry with
     */
    public JSONDataSourceBuilder(Map<String, Object> root, Function<Map<String, Object>, String> nameExtractor, Function<Map<String, Object>, String> unitExtractor, Function<Map<String, Object>, List<Map<String, Object>>> listExtractor, BiConsumer<Map<String, Object>, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	this.root.updateSupplier(() -> root);

	setNameExtractor(nameExtractor);
	setUnitExtractor(unitExtractor);
	setListExtractor(listExtractor);
	setDataExtractor(dataExtractor);
    }

    /**
     * Sets whether to cache the downloaded data or not
     * 
     * @param caches
     *            The bool
     * @return This builder
     */
    public JSONDataSourceBuilder setCaches(boolean caches)
    {
	source.setActive(caches);
	root.setActive(caches);
	list.setActive(caches);
	data.setActive(caches);
	name.setActive(caches);
	unit.setActive(caches);
	return this;
    }

    /**
     * Sets the list-extractor to use
     * 
     * @param listExtractor
     *            The list-extractor
     * @return This builder
     */
    public JSONDataSourceBuilder setListExtractor(Function<Map<String, Object>, List<Map<String, Object>>> listExtractor)
    {
	list.updateSupplier(() -> listExtractor.apply(root.get()));
	data.clearCache();
	return this;
    }

    /**
     * Sets the source supplier
     * 
     * @param sourceSupplier
     *            The source supplier
     * @return This builder
     */
    public JSONDataSourceBuilder setSourceSupplier(Supplier<String> sourceSupplier)
    {
	source.updateSupplier(sourceSupplier);
	root.clearCache();
	list.clearCache();
	data.clearCache();
	name.clearCache();
	unit.clearCache();
	return this;
    }

    /**
     * Sets the name extractor
     * 
     * @param nameExtractor
     *            The name extractor
     * @return This builder
     */
    public JSONDataSourceBuilder setNameExtractor(Function<Map<String, Object>, String> nameExtractor)
    {
	name.updateSupplier(() -> nameExtractor.apply(root.get()));
	return this;
    }

    /**
     * Sets the unit extractor
     * 
     * @param unitExtractor
     *            The unit extractor
     * @return This builder
     */
    public JSONDataSourceBuilder setUnitExtractor(Function<Map<String, Object>, String> unitExtractor)
    {
	unit.updateSupplier(() -> unitExtractor.apply(root.get()));
	return this;
    }

    /**
     * Sets the entry filter
     * 
     * @param entryFilter
     *            The entry filter
     * @return This builder
     */
    public JSONDataSourceBuilder setEntryFilter(Function<Map<String, Object>, Boolean> entryFilter)
    {
	this.entryFilter = entryFilter;
	data.clearCache();
	return this;
    }

    /**
     * Sets the data extractor
     * 
     * @param dataExtractor
     *            The data extractor
     * @return This builder
     */
    public JSONDataSourceBuilder setDataExtractor(BiConsumer<Map<String, Object>, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	this.dataExtractor = dataExtractor;
	data.clearCache();
	return this;
    }

    @Override
    protected Map<LocalDate, Double> generateData()
    {
	if (data.canGiveValue()) return data.get();

	if (!source.canGiveValue()) throw new DataSourceBuilderException("Missing source supplier!");
	if (!list.canGiveValue()) throw new DataSourceBuilderException("Missing list extractor!");
	if (dataExtractor == null) throw new DataSourceBuilderException("Missing data extractor!");

	Map<LocalDate, Double> result = new TreeMap<>();

	BiConsumer<LocalDate, Double> adder = (date, value) ->
	{
	    if(result.put(date, value) != null)
		System.out.println("Double!");
	};

	for (Map<String, Object> map : list.get())
	{
	    if (entryFilter == null || !entryFilter.apply(map)) dataExtractor.accept(map, adder);
	    // TODO: handle duplicated keys?
	}

	return result;
    }
}
