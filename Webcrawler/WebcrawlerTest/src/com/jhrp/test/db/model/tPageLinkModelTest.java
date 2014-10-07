package com.jhrp.test.db.model;

import com.jhrp.db.model.tPageLinkModel;
import com.jhrp.db.model.tPageModel;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

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
        tPageLinkModel t = new tPageLinkModel();
        /*String name = "supercooltestwebsite.com";
        String url = "/test";
        String host = "supercooltestwebsite.com";
        Timestamp last_update = this.now;
        Timestamp last_visit = this.now;
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
        }*/
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
