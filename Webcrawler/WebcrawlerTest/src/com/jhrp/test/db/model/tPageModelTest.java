package com.jhrp.test.db.model;

import com.jhrp.db.model.sPageModel;
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
        String name = "supercooltestwebsite.com";
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

            t.deleteLastPageTest(r_pid); //Question: Shall I be paranoid enough to test if a delete changed more than it should? ahah, no.
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

    @Test
    public void checkUpdatePage(){
        tPageModel t = new tPageModel();
        String name = "supercooltestwebsite.com";
        String url = "/test";
        String host = "supercooltestwebsite.com";
        Timestamp last_update = now;
        Timestamp last_visit = now;
        int n_visit = 1;
        int priority = 5;

        /*try {
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

    @Test
    public void checkArchivePage(){ //TODO must check page links and save them as well
        tPageModel t = new tPageModel();
        String name = "supercooltestwebsite.com_1";
        String url = "/test_1";
        String host = "supercooltestwebsite.com_1";
        Timestamp last_update = now;
        Timestamp last_visit = now;
        int n_visit = 1;
        int priority = 5;

        String name2 = "supercooltestwebsite.com_2";
        String url2 = "/test_2";
        String host2 = "supercooltestwebsite.com_2";
        Timestamp last_update2 = now;
        Timestamp last_visit2 = now;
        int n_visit2 = 1;
        int priority2 = 10;

        String name3 = "supercooltestwebsite.com_3";
        String url3 = "/test_3";
        String host3 = "supercooltestwebsite.com_3";
        Timestamp last_update3 = now;
        Timestamp last_visit3 = now;
        int n_visit3 = 1;
        int priority3 = 10;

        try {

            ResultSet r = t.insertNewPage(name, url, host, last_update, last_visit, n_visit, priority);
            ResultSet r2 = t.insertNewPage(name2, url2, host2, last_update2, last_visit2, n_visit2, priority2);
            ResultSet r3 = t.insertNewPage(name3, url3, host3, last_update3, last_visit3, n_visit3, priority3);

            assertTrue("Archiving failed, the number of records updated between tables was different.", t.archivePages(10));

            r.next();
            int r_pid = r.getInt(1);
            r2.next();
            int r_pid2 = r2.getInt(1);
            r3.next();
            int r_pid3 = r3.getInt(1);

            t.deleteLastPageTest(r_pid);
            sPageModel s = new sPageModel();
            s.deleteLastPageTest(r_pid2);
            s.deleteLastPageTest(r_pid3);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

}
