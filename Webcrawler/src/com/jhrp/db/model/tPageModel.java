package com.jhrp.db.model;

import com.jhrp.db.DBClientAccessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by joaopereira on 02/10/14.
 */
public class tPageModel extends DBClientAccessor {

    private static final String _columns = "p_id, name, url, host, first_visit, last_update, last_visit, n_visit, priority";

    public tPageModel(){
        this._table = "t_page";
    }

    public ResultSet getNextURL() throws SQLException {
        PreparedStatement t = this.conn.prepareStatement("SELECT url FROM " + _table + " ORDER BY priority DESC LIMIT 1");
        return t.executeQuery();
    }

    public ResultSet insertNewPage() throws SQLException {
        PreparedStatement t = this.conn.prepareStatement("SELECT url FROM " + _table + " ORDER BY priority DESC LIMIT 1");
        return t.executeQuery();
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
}
