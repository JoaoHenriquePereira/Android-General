package com.jhrp.db.model;

import com.jhrp.db.DBClientAccessor;

/**
 * Created by joaopereira on 03/10/14.
 */
public class sPageArchiveModel extends DBClientAccessor {

    private static final String _table = "t_pagebackup";
    private static final String _columns = "p_id, name, url, host, first_visit, last_update, last_visit, n_visit, priority";

}
