package com.jhrp.db.model;

import com.jhrp.db.DBClientAccessor;
import com.mysql.jdbc.Statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by joaopereira on 02/10/14.
 */
public class tPageModel extends DBClientAccessor {

    private static final String insert_columns = "name, url, host, last_update, last_visit, n_visit, priority";

    public tPageModel() {
        this._table = "t_page";
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

    public ResultSet updatePage() throws SQLException {
        PreparedStatement t = this.conn.prepareStatement("SELECT url FROM " + _table + " ORDER BY priority DESC LIMIT 1");
        return t.executeQuery();
    }

    /**
     * AKA move old records to archivePage to hasten insert process
     * @batch
     */
    public ResultSet archivePage() throws SQLException {
        PreparedStatement t = this.conn.prepareStatement("SELECT url FROM " + _table + " ORDER BY priority DESC LIMIT 1");
        return t.executeQuery();
    }

    /**
     *
     * @Test
     */

    public int deleteLastPageTest(int p_id) throws SQLException {
        PreparedStatement t = this.conn.prepareStatement("DELETE FROM " + _table + " WHERE " + _pkey + " = ?");
        t.setInt(1, p_id);
        return t.executeUpdate();
    }
}
