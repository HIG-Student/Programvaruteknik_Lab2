package se.hig.programvaruteknik.model;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import se.hig.programvaruteknik.model.DataSourceBuilder.DataSourceBuilderException;

@SuppressWarnings("javadoc")
public class TestDataSourceBuilder
{
    @Test(expected = DataSourceBuilderException.class)
    public void testMissingName()
    {
	new DataSourceBuilder()
	{
	    {
		setUnit("Unit");
	    }

	    @Override
	    protected Map<LocalDate, Double> generateData()
	    {
		return new TreeMap<>();
	    }
	}.build();
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingUnit()
    {
	new DataSourceBuilder()
	{
	    {
		setName("Name");
	    }

	    @Override
	    protected Map<LocalDate, Double> generateData()
	    {
		return new TreeMap<>();
	    }
	}.build();
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingUnitAndName()
    {
	new DataSourceBuilder()
	{
	    @Override
	    protected Map<LocalDate, Double> generateData()
	    {
		return new TreeMap<>();
	    }
	}.build();
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingData()
    {
	new DataSourceBuilder()
	{
	    {
		setUnit("Unit");
		setName("Name");
	    }

	    @Override
	    protected Map<LocalDate, Double> generateData()
	    {
		return null;
	    }
	}.build();
    }

    @Test
    public void testGenerateData()
    {
	String testName = "TestName";
	String testUnit = "TestUnit";

	Map<LocalDate, Double> data = new TreeMap<>();
	data.put(LocalDate.of(2016, 2, 27), 10d);
	data.put(LocalDate.of(2016, 2, 28), 10d);

	DataSource source = new DataSourceBuilder()
	{
	    {
		setName(testName);
		setUnit(testUnit);
	    }

	    @Override
	    protected Map<LocalDate, Double> generateData()
	    {
		return data;
	    }
	}.build();

	assertEquals(testName, source.getName());
	assertEquals(testUnit, source.getUnit());
	assertEquals(data, source.getData());
    }

    @Test
    public void testFilter()
    {
	Map<LocalDate, Double> inputData = new TreeMap<>();
	Map<LocalDate, Double> expectedData = new TreeMap<>();

	inputData.put(LocalDate.of(2016, 2, 27), 10d);
	expectedData.put(LocalDate.of(2016, 2, 27), 10d);

	inputData.put(LocalDate.of(2016, 2, 28), 20d);

	DataSource source = new DataSourceBuilder()
	{
	    {
		setName("Name");
		setUnit("Unit");
		setDataFilter((date, value) -> value.equals(20d));
	    }

	    @Override
	    protected Map<LocalDate, Double> generateData()
	    {
		return inputData;
	    }
	}.build();

	assertEquals(expectedData, source.getData());
    }
}
