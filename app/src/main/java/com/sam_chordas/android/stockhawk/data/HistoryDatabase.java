package com.sam_chordas.android.stockhawk.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by rakeshkalyankar on 24/04/16.
 */
@Database(version = HistoryDatabase.VERSION)
public class HistoryDatabase {
    private HistoryDatabase(){}

    public static final int VERSION = 1;

    @Table(HistoryColumns.class) public static final String HISTORY = "history";

}
