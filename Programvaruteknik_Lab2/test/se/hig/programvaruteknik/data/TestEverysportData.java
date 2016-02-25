package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import se.hig.programvaruteknik.data.FootballSource;
import se.hig.programvaruteknik.model.DataSource.DataSourceException;

@SuppressWarnings("javadoc")
public class TestEverysportData
{
    private FootballSource footballSource;

    @Before
    public void setUp() throws DataSourceException
    {
	footballSource = new FootballSource(
		"Football data",
		FootballSource.TOTAL_GOALS_EXTRACTOR,
		new File("test/se/hig/programvaruteknik/data/TestEverysportData.json"));
    }

    @Test
    public void testName() throws DataSourceException, IOException
    {
	assertEquals("Football data", footballSource.getName());
    }

    @Test
    public void testUnit() throws DataSourceException, IOException
    {
	assertEquals("Goals", footballSource.getUnit());
    }

    @Test
    public void testData() throws DataSourceException, IOException
    {
	Map<LocalDate, Double> data = footballSource.getData();

	assertEquals(new Double(30), new Double(data.size()));

	assertEquals(new Double(2 + 2), data.get(LocalDate.of(2014, 3, 30)));
	assertEquals(new Double(1 + 2), data.get(LocalDate.of(2014, 4, 6)));
	assertEquals(new Double(1 + 0), data.get(LocalDate.of(2014, 4, 12)));
	assertEquals(new Double(3 + 0), data.get(LocalDate.of(2014, 4, 17)));
	assertEquals(new Double(2 + 2), data.get(LocalDate.of(2014, 4, 21)));
	assertEquals(new Double(0 + 2), data.get(LocalDate.of(2014, 4, 28)));
	assertEquals(new Double(1 + 1), data.get(LocalDate.of(2014, 5, 5)));
	assertEquals(new Double(1 + 1), data.get(LocalDate.of(2014, 5, 8)));
	assertEquals(new Double(0 + 1), data.get(LocalDate.of(2014, 5, 11)));
	assertEquals(new Double(2 + 2), data.get(LocalDate.of(2014, 5, 19)));
	assertEquals(new Double(1 + 1), data.get(LocalDate.of(2014, 5, 26)));
	assertEquals(new Double(1 + 0), data.get(LocalDate.of(2014, 6, 2)));
	assertEquals(new Double(1 + 0), data.get(LocalDate.of(2014, 7, 5)));
	assertEquals(new Double(1 + 1), data.get(LocalDate.of(2014, 7, 13)));
	assertEquals(new Double(2 + 0), data.get(LocalDate.of(2014, 7, 19)));
	assertEquals(new Double(3 + 2), data.get(LocalDate.of(2014, 7, 26)));
	assertEquals(new Double(1 + 0), data.get(LocalDate.of(2014, 8, 4)));
	assertEquals(new Double(3 + 1), data.get(LocalDate.of(2014, 8, 10)));
	assertEquals(new Double(1 + 2), data.get(LocalDate.of(2014, 8, 13)));
	assertEquals(new Double(0 + 0), data.get(LocalDate.of(2014, 8, 16)));
	assertEquals(new Double(2 + 1), data.get(LocalDate.of(2014, 8, 25)));
	assertEquals(new Double(0 + 1), data.get(LocalDate.of(2014, 8, 30)));
	assertEquals(new Double(3 + 1), data.get(LocalDate.of(2014, 9, 14)));
	assertEquals(new Double(4 + 0), data.get(LocalDate.of(2014, 9, 19)));
	assertEquals(new Double(1 + 0), data.get(LocalDate.of(2014, 9, 24)));
	assertEquals(new Double(1 + 2), data.get(LocalDate.of(2014, 9, 29)));
	assertEquals(new Double(1 + 2), data.get(LocalDate.of(2014, 10, 4)));
	assertEquals(new Double(1 + 2), data.get(LocalDate.of(2014, 10, 19)));
	assertEquals(new Double(3 + 1), data.get(LocalDate.of(2014, 10, 25)));
	assertEquals(new Double(2 + 1), data.get(LocalDate.of(2014, 11, 1)));
    }
}
