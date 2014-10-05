package com.jhrp.test.db.model;

import com.jhrp.db.model.tPageBankModel;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * Created by joaopereira on 05/10/14.
 */
public class tPageBankModelTest {

    @Test
    public void checkLoadURLFromBank(){
        tPageBankModel t = new tPageBankModel();
        try {
            ResultSet r = t.getNextURL();
            int size = 0;
            while (r.next())
                size++;
            assertTrue("Value returned is 0, need at least 1 valid row in t_pagebank.", size > 0);
            assertTrue("Value returned is different than 1, got " + size + " instead.", 1 == size);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

    @Test
    public void checkLoadURLBatchFromBank(){
        tPageBankModel t = new tPageBankModel();
        int batch_size = 5;
        int offset = 0;
        try {
            ResultSet r = t.getNextURLBatch(batch_size, offset);
            int size = 0;
            while (r.next())
                size++;

            //########//
            assertTrue("Value returned is 0, need at least 1 valid rows in t_pagebank.", size > 0);
            assertTrue("Value returned is bigger than " + batch_size + ", got " + size + " instead.", batch_size >= size);
            //########//

        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError();
        }


    }

    @Test
    @Ignore("disabled until decision made on forcing minimum amount of URLs on database")
    public void checkLoadURLBatchFromBankWithOffset(){
        tPageBankModel t = new tPageBankModel();
        int batch_size = 5;
        int offset = 1;
        try {
            ResultSet r = t.getNextURLBatch(batch_size, offset);
            int size = 0;
            while (r.next())
                size++;

            //########//
            assertTrue("Value returned is 0, need at least 2 valid rows in t_pagebank to pass offset.", size > 0);
            assertTrue("Value returned is bigger than " + batch_size + ", got " + size + " instead.", batch_size >= size);
            //########//

        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }
}
