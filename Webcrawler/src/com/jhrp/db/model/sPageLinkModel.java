package com.jhrp.db.model;

import com.jhrp.db.DBClientAccessor;
import com.mysql.jdbc.Statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by joaopereira on 02/10/14.
 */
public class sPageLinkModel extends DBClientAccessor {

    private static final String insert_columns = "p_id, l_id";

    public sPageLinkModel() {
        this._table = "s_pagelink";
        this._pkey = "pl_id";
    }

    public ResultSet insertNewPageLink(int p_id, int l_id) throws SQLException {
        PreparedStatement t = this.conn.prepareStatement("INSERT INTO " + _table + " (" + insert_columns + ") VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS);
        t.setInt(1, p_id);
        t.setInt(2, l_id);
        t.executeUpdate();
        return t.getGeneratedKeys();
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
