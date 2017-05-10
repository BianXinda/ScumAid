package com.bxd.scumaid;

import android.provider.BaseColumns;

public final class ScrumDbSchema {
    private ScrumDbSchema() {}

    public static class ScrumEntry implements BaseColumns {
        public static final String TABLE_NAME = "scrumNames";
        public static final String COLUMN_NAME_EN = "nameEn";
        public static final String COLUMN_NAME_CN = "nameCn";
    }
}
