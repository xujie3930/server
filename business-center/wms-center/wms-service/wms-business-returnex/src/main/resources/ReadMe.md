### 退件流程
![img.png](img.png)

> 1. -- 客户端-退件预报
> 2. 新建数据 按处理状态通知wms仓库进行处理 如果是拆包检查 wms会把sku信息回调；拆包上架需要填写新sku ，进行上架 拆包检查等其余条件都需要wms回调
> > - 推送WMS （com.szmsd.returnex.controller.ReturnExpressOpenController.expectedCreate）
> > - WMS接收到货，回调OMS （com.szmsd.returnex.controller.ReturnExpressOpenController.saveArrivalInfoFormWms）
> >    -销毁
> > ### 处理
>
```mermaid
graph TD
N(处理方式)  --> A1[OMS退件通知] 
  A1 --> C2[重派]
  A1 --> C1[销毁]
    C2 --> D1[创建出库单]
N --> A3[退件预报]
 	A3 --> CK(拆包检查)
 	A3 --> ZB[整包上架]
 	A3 --> C1111[销毁]
      CK --> C11[销毁]
      CK --> C33[按明细上架]
N --> A2[WMS退件通知]
  A2 --> C111[销毁]
  A2 --> C3[整包上架]
  A2 --> CK1(拆包检查)
      CK1 --> CK1C33[按明细上架]
      CK1 --> CK1C11[销毁]
   

```