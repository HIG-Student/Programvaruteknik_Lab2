package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import se.hig.programvaruteknik.data.FootballGoalSource;
import se.hig.programvaruteknik.model.DataCollection;
import se.hig.programvaruteknik.model.DataCollectionBuilder;
import se.hig.programvaruteknik.model.DataSource;
import se.hig.programvaruteknik.model.MatchedDataPair;
import se.hig.programvaruteknik.model.Resolution;
import se.hig.programvaruteknik.model.DataSource.DataSourceException;

@SuppressWarnings("javadoc")
public class TestEverysportData
{
    public DataSource getWeatherData()
    {
	return new DataSource()
	{
	    @Override
	    public String getUnit()
	    {
		return "mm";
	    }

	    @Override
	    public String getName()
	    {
		return "Rain";
	    }

	    @SuppressWarnings("serial")
	    @Override
	    public Map<LocalDate, Double> getData()
	    {
		return Collections.unmodifiableMap(new HashMap<LocalDate, Double>()
		{
		    {
			put(LocalDate.of(2014, 3, 30), 5d);
		    }
		});
	    }
	};
    }

    public DataSource getFootballData() throws DataSourceException
    {
	return new FootballGoalSource("Football data", true, new File("data/test.json"));
    }

    @Test
    public void testData() throws DataSourceException, IOException
    {
	String date = Resolution.DAY.toKey(LocalDate.of(2014, 3, 30));

	DataCollection collection = new DataCollectionBuilder(getFootballData(), getWeatherData(), Resolution.DAY)
		.getResult();

	assertEquals(1, collection.getData().size());
	assertTrue(collection.getData().containsKey(date));

	MatchedDataPair pair = collection.getData().get(date);

	assertEquals(new Double(3), pair.getXValue());
    }
}
