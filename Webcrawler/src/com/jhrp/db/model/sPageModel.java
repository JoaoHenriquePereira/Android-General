package com.jhrp.db.model;

import com.jhrp.db.DBClientAccessor;
import com.mysql.jdbc.Statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by joaopereira on 03/10/14.
 */
public class sPageModel extends DBClientAccessor {

    private static final String insert_columns = "name, url, host, last_update, last_visit, n_visit, priority";

    public sPageModel() {
        this._table = "s_page";
        this._pkey = "p_id";
    }

    public ResultSet insertNewPage(String name,
                                   String url,
                                   String host,
                                   Timestamp last_update,
                                   Timestamp last_visit,
                                   int n_visit,
                                   int priority) throws SQLException {
        PreparedStatement t = this.conn.prepareStatement("INSERT INTO " + _table + " (" + insert_columns + ") VALUES (?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        t.setString(1, name);
        t.setString(2, url);
        t.setString(3, host);
        t.setTimestamp(4, last_update);
        t.setTimestamp(5, last_visit);
        t.setInt(6, n_visit);
        t.setInt(7, priority);
        t.executeUpdate();
        return t.getGeneratedKeys();
    }
}
