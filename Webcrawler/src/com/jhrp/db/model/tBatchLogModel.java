package com.jhrp.db.model;

import com.jhrp.db.DBClientAccessor;

/**
 * Created by joaopereira on 09/10/14.
 */
public class tBatchLogModel extends DBClientAccessor {

    private static final String insert_columns = "object, status, insert_date";

    public tBatchLogModel(){
        this._table = "t_batchlog";
        this._pkey = "bl_id";
    }


}
