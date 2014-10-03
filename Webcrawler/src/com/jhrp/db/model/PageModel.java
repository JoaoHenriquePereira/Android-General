package com.jhrp.db.model;

import com.jhrp.db.DBClientAccessor;

/**
 * Created by joaopereira on 02/10/14.
 */
public class PageModel extends DBClientAccessor {

    private static final String _table = "t_page";
    private static final String _columns = "p_id, name, url, host, first_visit, last_update, last_visit, n_visit, priority";


}
