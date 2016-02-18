package se.hig.programvaruteknik;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * A source of JSON data
 */
public class JSONSource extends UnmodifiableDataSource
{
    public JSONSource()
    {
	
    }
    
    @Override
    public String getName()
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getUnit()
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    protected Map<LocalDate, Double> getRawData()
    {
	// TODO Auto-generated method stub
	return null;
    }

    public interface JSONExtractor<T>
    {
	public T extract(Map<String, Object> json);
    }
}
