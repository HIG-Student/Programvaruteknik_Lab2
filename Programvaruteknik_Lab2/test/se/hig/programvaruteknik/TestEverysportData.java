package se.hig.programvaruteknik;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import se.hig.programvaruteknik.DataSource.DataSourceException;

@SuppressWarnings("javadoc")
public class TestEverysportData
{
    public DataSource getWeatherData()
    {
	return new UnmodifiableDataSource()
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
	    protected Map<LocalDate, Double> getRawData()
	    {
		return new HashMap<LocalDate, Double>()
		{
		    {
			put(LocalDate.of(2014, 3, 30), 5d);
		    }
		};
	    }
	};
    }

    public DataSource getFootballData() throws DataSourceException, IOException
    {
	return FootballGoalSource.fromJSON(
		"Football data",
		Files.readAllLines(new File("data/test.json").toPath()).stream().collect(Collectors.joining()),
		true);
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
