package com.jhrp.test.db.model;

import com.jhrp.db.model.tPageBankModel;
import com.jhrp.db.model.tPageModel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.transform.Result;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by joaopereira on 05/10/14.
 */
public class tPageModelTest {

    private Timestamp now = null;

    @Before
    public void before(){
        Calendar calendar = Calendar.getInstance();
        this.now = new Timestamp(calendar.getTime().getTime());
    }

    @Test
    public void checkInsertNewPage(){
        tPageModel t = new tPageModel();
        String name = "supercoolwebsite.com";
        String url = "";
        String host = "";
        Timestamp last_update = now;
        Timestamp last_visit = now;
        int n_visit = 1;
        int priority = 5;

        try {
            ResultSet r = t.insertNewPage(name, url, host, last_update, last_visit, n_visit, priority);
            r.next();
            int r_pid = r.getInt(1);

            assertFalse("Insert failed on t_page.", r_pid < 1);

            t.deleteLastPageTest(r_pid); //TODO Shall I be paranoid enough to test if a delete changed more than it should? ahah, no.
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

    @Test
    public void checkUpdatePage(){

    }


}
