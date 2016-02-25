package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

import se.hig.programvaruteknik.data.CSVDataSource_Old;

@SuppressWarnings("javadoc")
public class TestCSVDataSource
{
    @Test
    public void testCreate()
    {
	File csv = new File("data/smhi-gavle-regn-1954-2015.csv");
	assertTrue("Can't find file!", csv.exists());

	// CSVDataSource(DataSupplierFactory.createFileFetcher("data/smhi-gavle-regn-1954-2015.csv"),
	// Function<String, String> nameExtractor, Function<String, String>
	// unitExtractor, Function<String, List<String>> rowExtractor,
	// BiConsumer<String, BiConsumer<LocalDate, Double>> dataExtractor)

	CSVDataSource_Old smhi = new CSVDataSource_Old(csv.getPath());
	assertEquals("Incorrect name", "Regn i Gävle", smhi.getName());
	assertEquals("Incorrect Unit", "mm", smhi.getUnit());
	Map<LocalDate, Double> data = smhi.getData();
	assertEquals("Incorrect size", new Integer(21896), new Integer(data.size()));
    }
}
