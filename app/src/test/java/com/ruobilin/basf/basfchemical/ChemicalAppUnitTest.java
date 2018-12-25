package com.ruobilin.basf.basfchemical;

import org.junit.Test;

import static org.junit.Assert.*;
import com.ruobilin.basf.basfchemical.*;


public class ChemicalAppUnitTest {
    @Test
    public  void test_getDBFileName(){
        String sShould = "/data/data/com.ruobilin.basf.basfchemical/database/chemical.db";
        assertEquals(sShould, ChemicalApp.getInstance().getDBFileName());
    }
}
