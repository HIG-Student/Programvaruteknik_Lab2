package se.hig.programvaruteknik.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import se.hig.programvaruteknik.data.SMHISourceBuilder.Period;
import se.hig.programvaruteknik.model.DataCollectionBuilder;
import se.hig.programvaruteknik.model.DataSource;
import se.hig.programvaruteknik.model.MatchedDataPair;
import se.hig.programvaruteknik.model.MergeType;
import se.hig.programvaruteknik.model.Resolution;

/**
 * Class that combine football and weather data
 */
public class FootballAndWeatherCombiner
{
    private Map<String, SMHILocation> arenaToLocationMapper;

    /**
     * Set the arena to location mapping
     * 
     * @param arenaToLocationMapper
     *            The mapping
     * @return This builder
     */
    public FootballAndWeatherCombiner setArenaToLocationMapper(Map<String, SMHILocation> arenaToLocationMapper)
    {
	this.arenaToLocationMapper = arenaToLocationMapper;
	return this;
    }

    /**
     * Build that data
     * 
     * @return The built data
     */
    public List<MatchedDataPair> build()
    {
	List<MatchedDataPair> result = new ArrayList<>();

	FootballSourceBuilder footballBuilder = new FootballSourceBuilder();
	footballBuilder.setFetchFromWebsite();
	footballBuilder.setName("Football goals");
	footballBuilder.setUnit("Goals");
	footballBuilder.setDataExtractor(FootballSourceBuilder.TOTAL_GOALS_EXTRACTOR);

	for (SMHILocation location : arenaToLocationMapper.values())
	{
	    SMHISourceBuilder weatherBuilder = new SMHISourceBuilder(SMHISourceBuilder.DataType.TEMPERATURE, location);
	    weatherBuilder.setPeriod(Period.OLD);

	    footballBuilder.setEntryFilter((entry) ->
	    {
		@SuppressWarnings("unchecked")
		Map<String, Object> arena = (Map<String, Object>) ((Map<String, Object>) entry.get("facts"))
			.get("arena");
		return (arena == null || !arenaToLocationMapper
			.containsKey(arena.get("id").toString()) || arenaToLocationMapper
				.get(arena.get("id").toString()) != location);
	    });

	    DataSource weather = weatherBuilder.build();
	    DataSource goals = footballBuilder.build();

	    DataCollectionBuilder builder = new DataCollectionBuilder(goals, weather, Resolution.DAY);
	    builder.setXMergeType(MergeType.SUM);
	    builder.setYMergeType(MergeType.AVERAGE);
	    Collection<MatchedDataPair> pairs = builder.getResult().getData().values();
	    result.addAll(pairs);
	}

	return Collections.unmodifiableList(result);
    }

    /**
     * Entry point
     * 
     * @param args
     *            Arguments
     */
    public static void main(String[] args)
    {
	Map<String, SMHILocation> locationMapping = new TreeMap<>();
	locationMapping.put("63057", SMHILocation.KALMAR_FLYGPLATS);
	locationMapping.put("61401", SMHILocation.HALMSTAD);
	locationMapping.put("61383", SMHILocation.MALMÖ_A);
	locationMapping.put("61382", SMHILocation.KARLSHAMN);
	locationMapping.put("61378", SMHILocation.BORÅS);
	locationMapping.put("60907", SMHILocation.NORRKÖPING_SMHI);
	locationMapping.put("60662", SMHILocation.HELSINGBORG_A);
	locationMapping.put("60659", SMHILocation.ULLARED_A);
	locationMapping.put("60649", SMHILocation.ÖREBRO_A);
	locationMapping.put("60610", SMHILocation.GÄVLE_A);
	locationMapping.put("60029", SMHILocation.NORRKÖPING_SMHI);
	locationMapping.put("110637", SMHILocation.GÖTEBORG_A);
	locationMapping.put("61381", SMHILocation.GÖTEBORG_A);
	locationMapping.put("32736", SMHILocation.GÖTEBORG_A);
	locationMapping.put("110645", SMHILocation.STOCKHOLM_A);
	locationMapping.put("110511", SMHILocation.STOCKHOLM_A);
	locationMapping.put("5184", SMHILocation.STOCKHOLM_A);
	locationMapping.put("110295", SMHILocation.STOCKHOLM_A);
	locationMapping.put("18", SMHILocation.STOCKHOLM_A);
	locationMapping.put("13", SMHILocation.STOCKHOLM_A);

	FootballAndWeatherCombiner combiner = new FootballAndWeatherCombiner();
	combiner.setArenaToLocationMapper(locationMapping);
	List<MatchedDataPair> pairs = combiner.build();

	System.out.println("Goals made at temperatures");
	System.out.println("(Goals : Temperature)");
	System.out.println("");
	for (MatchedDataPair pair : pairs)
	{
	    System.out.println(pair);
	}
    }
}
