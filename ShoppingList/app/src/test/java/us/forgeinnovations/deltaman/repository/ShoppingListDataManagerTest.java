package us.forgeinnovations.deltaman.repository;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import us.forgeinnovations.deltaman.models.shop.ProductInfo;

import static org.junit.Assert.*;

/**
 * Created by RGautam on 11/28/2017.
 */
public class ShoppingListDataManagerTest {
//TODO: Add more tests to this
    public static final String MY_TEST_ID = "MY_TEST_ID";

    @Test
    public void createNewNote() throws Exception {
        List<ProductInfo> prodInfoList= new ArrayList<ProductInfo>();

        ProductInfo prodInfo = new ProductInfo(MY_TEST_ID,"Android with Intents");
        assertEquals("Android with Intents", prodInfo.getTitle());
    }

}