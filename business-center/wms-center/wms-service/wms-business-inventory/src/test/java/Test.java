import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.inventory.BusinessInventoryApplication;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.mapper.InventoryRecordMapper;
import com.szmsd.inventory.service.IInventoryRecordService;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Test
 * @Description:
 * @Author: 11
 * @Date: 2021-07-30 15:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessInventoryApplication.class)
public class Test {
    @Resource
    private IInventoryRecordService iInventoryRecordService;
    @Resource
    private InventoryRecordMapper inventoryRecordMapper;
    @org.junit.Test
    public void  run(){
        System.out.println(1);
        List<String> warehouseNoList = new ArrayList<>();
        warehouseNoList.add("");
        String sku = "";
        QueryWrapper<InventoryRecord> sku1 = new QueryWrapper<InventoryRecord>().eq("sku", sku).select("id");
        InventoryRecord inventoryRecord = inventoryRecordMapper.selectById(1);
        List<InventoryRecord> inventoryRecordVOS = inventoryRecordMapper
                .selectList(sku1);

    }
}
