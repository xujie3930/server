import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.http.config.CkConfig;
import com.szmsd.inventory.BusinessInventoryApplication;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.job.InventoryJobService;
import com.szmsd.inventory.mapper.InventoryRecordMapper;
import com.szmsd.inventory.service.IInventoryRecordService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TestUnit
 * @Description:
 * @Author: 11
 * @Date: 2021-07-30 15:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessInventoryApplication.class)
public class TestUnit {
    @Resource
    private IInventoryRecordService iInventoryRecordService;
    @Resource
    private InventoryRecordMapper inventoryRecordMapper;

    @Test
    public void run() {
        System.out.println(1);
        List<String> warehouseNoList = new ArrayList<>();
        warehouseNoList.add("");
        String sku = "";
        QueryWrapper<InventoryRecord> sku1 = new QueryWrapper<InventoryRecord>().eq("sku", sku).select("id");
        InventoryRecord inventoryRecord = inventoryRecordMapper.selectById(1);
        List<InventoryRecord> inventoryRecordVOS = inventoryRecordMapper
                .selectList(sku1);

    }

    @Resource
    InventoryJobService inventoryJobService;

    /**
     * 库存对比
     */
    @SneakyThrows
    @Test
    public void asyncInventoryWarning() {
        inventoryJobService.asyncInventoryWarning();
        Thread.sleep(10 * 1000 * 60);
    }

    @SneakyThrows
    @Test
    public void asyncInventoryWarning2() {
        List<InventoryJobService.WarehouseSkuCompare> cnid73 = inventoryJobService.inventoryWarning("CNID73");
        System.out.println(JSONObject.toJSONString(cnid73));
        Thread.sleep(10 * 1000 * 60);
    }
}
