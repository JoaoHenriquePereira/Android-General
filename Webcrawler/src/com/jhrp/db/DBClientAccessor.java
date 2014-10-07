package com.jhrp.db;

import com.jhrp.db.DBClient;

/**
 * Created by joaopereira on 03/10/14.
 */
public class DBClientAccessor extends DBClient {

    //TODO proper management of dbclient
    protected String _table = "";
    protected String _pkey = "";

    protected String getTableName(){
        return _table;
    }

    public String getPKey(){
        return _pkey;
    }

    public String getStorageTableName(){
        return _table.replaceFirst("t_","s_");
    }

}
