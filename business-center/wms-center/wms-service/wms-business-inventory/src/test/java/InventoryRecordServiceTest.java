import com.alibaba.fastjson.JSON;
import com.szmsd.inventory.BusinessInventoryApplication;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import com.szmsd.inventory.service.IInventoryRecordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessInventoryApplication.class)
public class InventoryRecordServiceTest {

    @Resource
    private IInventoryRecordService iInventoryRecordService;

    @Test
    public void selectSkuVolume(){

        InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO = new InventorySkuVolumeQueryDTO();
        inventorySkuVolumeQueryDTO.setSku("SCNID73000200");
        inventorySkuVolumeQueryDTO.setWarehouseCode("NJ");

        List<InventorySkuVolumeVO> skuVolumeVOS = iInventoryRecordService.selectSkuVolume(inventorySkuVolumeQueryDTO);

        System.out.println(JSON.toJSONString(skuVolumeVOS));

    }
}
