package com.elishai.officemisdeeds.DB;

public class DBScheme {
    public static final class MisdeedTable {
        public static final String NAME = "misdeeds";

        public static final class Cols {
            public static final String UUID     = "uuid";
            public static final String TITLE    = "title";
            public static final String DATE     = "date";
            public static final String SOLVED   = "solved";
            public static final String SUSPECT  = "suspect";
        }
    }
}
