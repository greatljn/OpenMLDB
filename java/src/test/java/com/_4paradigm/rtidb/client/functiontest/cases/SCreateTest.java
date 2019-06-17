package com._4paradigm.rtidb.client.functiontest.cases;

import com._4paradigm.rtidb.client.base.TestCaseBase;
import com._4paradigm.rtidb.client.ha.TableHandler;
import com._4paradigm.rtidb.client.schema.ColumnDesc;
import com._4paradigm.rtidb.client.schema.ColumnType;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Listeners({com._4paradigm.rtidb.client.functiontest.utils.TestReport.class})
public class SCreateTest extends TestCaseBase {

    private final static AtomicInteger id = new AtomicInteger(20);
    @BeforeClass
    public void setUp() {
        setUp();
    }

    @AfterClass
    public void tearDown() {
        tearDown();
    }
    public static String genLongString(int len) {
        String str = "";
        for (int i = 0; i < len; i++) {
            str += "a";
        }
        return str;
    }

    @DataProvider(name = "schema")
    public Object[][] Schema() {
        return new Object[][]{
                new Object[][]{{true},
                        {true, "card", ColumnType.kString},
                        {false, "card1", ColumnType.kString},
                        {false, "amt", ColumnType.kDouble}},
                new Object[][]{{true},
                        {true, "card", ColumnType.kString},
                        {true, "card1", ColumnType.kString},
                        {true, "amt", ColumnType.kString}},
                new Object[][]{{true},
                        {false, "card", ColumnType.kString},
                        {false, "card1", ColumnType.kString},
                        {false, "amt", ColumnType.kString}},
                new Object[][]{{false},
                        {true, "card", ColumnType.kString},
                        {false, " ", ColumnType.kString},
                        {false, "amt", ColumnType.kString}},
                new Object[][]{{false},
                        {true, " ", ColumnType.kString},
                        {false, "card1", ColumnType.kString},
                        {false, "amt", ColumnType.kString}},
                new Object[][]{{false},
                        {true, "card", ColumnType.kString},
                        {false, "", ColumnType.kString},
                        {false, "amt", ColumnType.kString}},
                new Object[][]{{false},
                        {true, "", ColumnType.kString},
                        {false, "card1", ColumnType.kString},
                        {false, "amt", ColumnType.kString}},
                new Object[][]{{false},
                        {false, "card", ColumnType.kDouble},
                        {false, "card", ColumnType.kString}},
                new Object[][]{{true}, {false, "card", ColumnType.kString}},
                new Object[][]{{false}, {true, "", ColumnType.kString}},
                new Object[][]{{false}, {true, "   ", ColumnType.kString}},
                new Object[][]{{true}, {true, genLongString(128), ColumnType.kString}},
                new Object[][]{{true}, {false, genLongString(128), ColumnType.kString}},
                new Object[][]{{true},
                        {true, genLongString(100), ColumnType.kString},
                        {true, genLongString(29), ColumnType.kString}},
                new Object[][]{{false}, {true, genLongString(129), ColumnType.kString}},
                new Object[][]{{true},
                        {true, "card", ColumnType.kFloat},
                        {false, "amt", ColumnType.kString}},
                new Object[][]{{true},
                        {true, "card", ColumnType.kInt32},
                        {false, "amt", ColumnType.kString}},
                new Object[][]{{true},
                        {true, "card", ColumnType.kInt64},
                        {false, "amt", ColumnType.kString}},
                new Object[][]{{true},
                        {true, "card", ColumnType.kUInt32},
                        {false, "amt", ColumnType.kString}},
        };
    }


    @Test(dataProvider = "schema")
    public void testCreate(Object[]... array) {
        int tid = id.incrementAndGet();
        Boolean result = (Boolean) array[0][0];
        List<ColumnDesc> schema = new ArrayList<ColumnDesc>();
        int indexes = 0;
        int schemaCount = array.length - 1;
        for (int i = 1; i < array.length; i++) {
            Object[] o = array[i];
            ColumnDesc desc = new ColumnDesc();
            Boolean index = (Boolean) o[0];
            if (index) {
                indexes++;
            }
            desc.setAddTsIndex(index);
            desc.setName((String) o[1]);
            desc.setType((ColumnType) o[2]);
            schema.add(desc);
        }
        Boolean ok = tabletClient.createTable("tj0", tid, 0, 0, 8, schema);
        System.out.println(ok);
        Assert.assertEquals(ok, result);
        if (ok) {
            TableHandler th = snc.getHandler(tid);
            Assert.assertEquals(th.getSchema().size(), schemaCount);
            Assert.assertEquals(th.getIndexes().size(), indexes);
        }
    }
}
