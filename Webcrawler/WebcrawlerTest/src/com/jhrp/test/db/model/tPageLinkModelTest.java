package com.jhrp.test.db.model;

import com.jhrp.db.model.tPageLinkModel;
import com.jhrp.db.model.tPageModel;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;

/**
 * Created by joaopereira on 05/10/14.
 */
public class tPageLinkModelTest {
//TODO do not forget to cleanup pagelink as well
    private Timestamp now = null;

    @Before
    public void before(){
        Calendar calendar = Calendar.getInstance();
        this.now = new Timestamp(calendar.getTime().getTime());
    }

    @Test
    public void checkInsertNewPageLink(){
        tPageModel t = new tPageModel();
        tPageLinkModel tt = new tPageLinkModel();
        String name = "supercooltestwebsite.com";
        String url = "/test";
        String host = "supercooltestwebsite.com";
        Timestamp last_update = this.now;
        Timestamp last_visit = this.now;
        int n_visit = 1;
        int priority = 5;

        String name2 = "supercooltestwebsite.com_2";
        String url2 = "/test_2";
        String host2 = "supercooltestwebsite.com_2";
        Timestamp last_update2 = now;
        Timestamp last_visit2 = now;
        int n_visit2 = 1;
        int priority2 = 10;

        try {
            int r_pid = 0;
            int r_pid2 = 0;
            ResultSet r = t.getLastPKey();
            if(r.isBeforeFirst()) {   //The table might be empty so we don't make it a requirement for testing
                r.next();
                r_pid = r.getInt(1);
            }

            try{
                r = tt.insertNewPageLink(r_pid + 1, r_pid + 2);
                fail("Should have thrown a MySQLIntegrityConstraintViolationException testing insert pagelink.");
            } catch (MySQLIntegrityConstraintViolationException e){
                //As expected
            }

            r = t.insertNewPage(name, url, host, last_update, last_visit, n_visit, priority);
            ResultSet r2 = t.insertNewPage(name2, url2, host2, last_update2, last_visit2, n_visit2, priority2);

            r.next();
            r_pid = r.getInt(1);
            r2.next();
            r_pid2 = r2.getInt(1);

            int r_pid3 = 0;

            r = tt.insertNewPageLink(r_pid, r_pid2);

            if(r.isBeforeFirst()) {   //The table might be empty so we don't make it a requirement for testing
                r.next();
                r_pid3 = r.getInt(1);
            }

            assertFalse("Insert failed on t_pagelink.", r_pid3 < 1);

            tt.deleteLastPageTest(r_pid3);
            t.deleteLastPageTest(r_pid);
            t.deleteLastPageTest(r_pid2);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

    @Test
    public void checkUpdatePageLink(){
        tPageLinkModel t = new tPageLinkModel();
        /*String name = "supercooltestwebsite.com";
        String url = "/test";
        String host = "supercooltestwebsite.com";
        Timestamp last_update = now;
        Timestamp last_visit = now;
        int n_visit = 1;
        int priority = 5;

        try {
            ResultSet r = t.insertNewPage(name, url, host, last_update, last_visit, n_visit, priority);
            r.next();
            int r_pid = r.getInt(1);

            assertFalse("Insert failed on t_page.", r_pid < 1);

            ResultSet c = t.updatePage(name, url, host, last_update, last_visit, n_visit, priority);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError();
        }*/
    }


}
