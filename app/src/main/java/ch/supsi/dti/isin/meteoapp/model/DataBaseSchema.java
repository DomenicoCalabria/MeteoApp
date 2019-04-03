package ch.supsi.dti.isin.meteoapp.model;

public class DataBaseSchema {
    public static final class Locations {
        public static final String NAME = "Locazioni";

        //Oltre all'id, anche il nome di una località sarà univoco (programmaticamente)
        public static final class Cities {
            //public static final String SERVICE_ID = "codID";
            public static final String CITY_NAME = "nome_città";
            public static final String LATITUDE = "latitudine";
            public static final String LONGITUDE = "longitudine";
        }
    }
}
