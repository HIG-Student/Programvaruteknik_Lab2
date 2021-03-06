
package se.hig.programvaruteknik;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class TestJSONFormatter
{
    @Test
    public void testObjectBreaks()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("{\n\t\n}", formatter.format("{}"));
    }

    @Test
    public void testArrayBreaks()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("[\n\t\n]", formatter.format("[]"));
    }

    @Test
    public void testNotMessWithString()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("\"{[,]}\"", formatter.format("\"{[,]}\""));
    }

    @Test
    public void testNotMessWithNumber()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("-34.67e-7", formatter.format("-34.67e-7"));
    }

    @Test
    public void testFixSpacesInNumber()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("-34.67e-7", formatter.format("- 3 4. 67 e- 7"));
    }
}
