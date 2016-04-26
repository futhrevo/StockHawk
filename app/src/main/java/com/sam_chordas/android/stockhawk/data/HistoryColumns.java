package com.sam_chordas.android.stockhawk.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by rakeshkalyankar on 24/04/16.
 */
public class HistoryColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    public static final String SYMBOL = "symbol";

    @DataType(DataType.Type.TEXT)
    public static final String CREATED = "created";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String HISTORICAL_DATA = "pastData";
}
