package se.hig.programvaruteknik.model;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A builder for {@link DataCollection}s<br>
 * <br>
 * Merge types defaults to {@link MergeType#SUM}
 */
public class DataCollectionBuilder
{
    private MergeType xMergeType = MergeType.SUM;
    private MergeType yMergeType = MergeType.SUM;
    private String title = null;
    private DataSource xData;
    private DataSource yData;
    private Resolution resolution;

    private CachedValue<Map<String, MatchedDataPair>> resultingData = new CachedValue<>(
	    () -> matchData(xData, xMergeType, yData, yMergeType, resolution));

    /**
     * Creation of a builder that builds a {@link DataCollection}
     * 
     * @param xData
     *            The x values
     * @param yData
     *            The y values
     * @param resolution
     *            The resolution
     */
    public DataCollectionBuilder(DataSource xData, DataSource yData, Resolution resolution)
    {
	this.xData = xData;
	this.yData = yData;
	this.resolution = resolution;
    }

    /**
     * Sets the title
     * 
     * @param title
     *            The new title
     * @return this builder (for chaining)
     */
    public DataCollectionBuilder setTitle(String title)
    {
	this.title = title;
	return this;
    }

    /**
     * Sets the resolution
     * 
     * @param resolution
     *            The new resolution
     * @return this builder (for chaining)
     */
    public DataCollectionBuilder setResolution(Resolution resolution)
    {
	if (!this.resolution.equals(resolution)) resultingData.clearCache();

	this.resolution = resolution;
	return this;
    }

    /**
     * Gets the title
     * 
     * @return The title
     */
    public String getTitle()
    {
	return title == null ? (xData.getName() + " : " + yData.getName()) : title;
    }

    /**
     * Sets how to merge the x values
     * 
     * @param xMergeType
     *            The merge type
     * @return this builder (for chaining)
     */
    public DataCollectionBuilder setXMergeType(MergeType xMergeType)
    {
	if (!this.xMergeType.equals(xMergeType)) resultingData.clearCache();

	this.xMergeType = xMergeType;
	return this;
    }

    /**
     * Sets how to merge the y values
     * 
     * @param yMergeType
     *            The merge type
     * @return this builder (for chaining)
     */
    public DataCollectionBuilder setYMergeType(MergeType yMergeType)
    {
	if (!this.yMergeType.equals(yMergeType)) resultingData.clearCache();

	this.yMergeType = yMergeType;
	return this;
    }

    static List<String> collectKeys(DataSource source, Resolution resolution)
    {
	return source.getData().keySet().stream().map(resolution::toKey).distinct().collect(Collectors.toList());
    }

    static Map<String, MatchedDataPair> matchData(DataSource xSource, MergeType xMergeType, DataSource ySource, MergeType yMergeType, Resolution resolution)
    {
	List<String> xKeys = collectKeys(xSource, resolution);
	List<String> yKeys = collectKeys(ySource, resolution);

	List<String> commonKeys = xKeys.stream().filter((key) -> yKeys.contains(key)).collect(Collectors.toList());

	Function<DataSource, Map<String, List<Double>>> dataOrganizer = (source) -> source
		.getData()
		.entrySet()
		.stream()
		.filter((entry) -> commonKeys.contains(resolution.toKey(entry)))
		.collect(
			Collectors.groupingBy(
				(entry) -> resolution.toKey(entry),
				Collectors.mapping((entry) -> entry.getValue(), Collectors.toList())));

	Map<String, List<Double>> xData = dataOrganizer.apply(xSource);
	Map<String, List<Double>> yData = dataOrganizer.apply(ySource);

	Map<String, MatchedDataPair> matches = commonKeys.stream().collect(
		Collectors.<String, String, MatchedDataPair> toMap(
			key -> key,
			key -> new MatchedDataPair(
				xMergeType.merge(xData.get(key)),
				yMergeType.merge(yData.get(key)))));

	return matches;
    }

    /**
     * Build the {@link DataCollection}
     * 
     * @return the resulting {@link DataCollection}
     */
    public DataCollection getResult()
    {
	return new DataCollection(getTitle(), xData.getUnit(), yData.getUnit(), resultingData.get());
    }
}
