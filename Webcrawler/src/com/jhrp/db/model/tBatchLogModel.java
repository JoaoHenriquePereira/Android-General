package com.jhrp.db.model;

import com.jhrp.db.DBClientAccessor;
import com.mysql.jdbc.Statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by joaopereira on 09/10/14.
 */
public class tBatchLogModel extends DBClientAccessor {

    private static final String insert_columns = "object";

    private static final String update_columns = "object, status";

    public tBatchLogModel(){
        this._table = "t_batchlog";
        this._pkey = "bl_id";
    }

    /**
     * Inserts new record on batch log, notice all values have a default except object
     *
     */
    public ResultSet insertNewBatchLog(String object) throws SQLException {
        PreparedStatement t = this.conn.prepareStatement("INSERT INTO " + _table + " (" + insert_columns + ") VALUES (?)",
                Statement.RETURN_GENERATED_KEYS);
        t.setString(1, object);
        t.executeUpdate();
        return t.getGeneratedKeys();
    }

    public ResultSet updateBatchLog(String object,
                                       int status) throws SQLException {
        PreparedStatement t = this.conn.prepareStatement("UPDATE " + _table + " (" + insert_columns + ") VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS);
        t.setString(1, object);
        t.setInt(2, status);
        t.executeUpdate();
        return t.getGeneratedKeys();
    }
}
