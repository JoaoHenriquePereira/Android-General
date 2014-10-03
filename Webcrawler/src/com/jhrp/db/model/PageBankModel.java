package com.jhrp.db.model;

import com.jhrp.db.DBClientAccessor;

import java.sql.*;

/**
 * Created by joaopereira on 03/10/14.
 */
public class PageBankModel extends DBClientAccessor {

    private static final String default_columns = "pb_id, url, priority";

    public PageBankModel (){
        this._table = "t_pagebank";
    }

    public ResultSet getNextURL() throws SQLException {
        PreparedStatement t = this.conn.prepareStatement("SELECT url FROM " + _table + " ORDER BY priority DESC LIMIT 1");
        return t.executeQuery();
    }

}
