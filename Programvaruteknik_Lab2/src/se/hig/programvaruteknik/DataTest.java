package se.hig.programvaruteknik;

import java.util.Map;
import java.util.TreeMap;

import se.hig.programvaruteknik.data.SMHILocation;

public class DataTest
{
    public static void main(String[] args)
    {
	Map<String, SMHILocation> locationMapping = new TreeMap<>();
	locationMapping.put("63057", SMHILocation.KALMAR);
	locationMapping.put("61401", SMHILocation.HALMSTAD);
	locationMapping.put("61383", SMHILocation.MALMÖ_A);
	locationMapping.put("61382", SMHILocation.KARLSHAMN);
	locationMapping.put("61378", SMHILocation.BORÅS);
	locationMapping.put("60907", SMHILocation.ÅTVIDABERG);
	locationMapping.put("60662", SMHILocation.HELSINGBORG_A);
	locationMapping.put("60659", SMHILocation.FALKENBERG);
	locationMapping.put("60649", SMHILocation.ÖREBRO_A);
	locationMapping.put("60610", SMHILocation.GÄVLE_A);
	locationMapping.put("60029", SMHILocation.NORRKÖPING);
	locationMapping.put("110637", SMHILocation.GÖTEBORG_A);
	locationMapping.put("61381", SMHILocation.GÖTEBORG_A);
	locationMapping.put("32736", SMHILocation.GÖTEBORG_A);
	locationMapping.put("110645", SMHILocation.STOCKHOLM_A);
	locationMapping.put("110511", SMHILocation.STOCKHOLM_A);
	locationMapping.put("5184", SMHILocation.STOCKHOLM_A);
	locationMapping.put("110295", SMHILocation.STOCKHOLM_A);
	locationMapping.put("18", SMHILocation.STOCKHOLM_A);
	locationMapping.put("13", SMHILocation.STOCKHOLM_A);
	
	
    }
}
