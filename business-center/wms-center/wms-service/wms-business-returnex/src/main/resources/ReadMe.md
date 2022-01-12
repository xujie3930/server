### 退件流程
![img.png](img.png)

> 1. -- 客户端-退件预报
> > - 推送WMS （com.szmsd.returnex.controller.ReturnExpressOpenController.expectedCreate）
> > - WMS接收到货，回调OMS （com.szmsd.returnex.controller.ReturnExpressOpenController.saveArrivalInfoFormWms）
> >    -销毁
> > ### 处理
>
```mermaid
graph TD
A[WMS退件-无名件] --> B(退件列表) 
AAAA[客户退件] --> B(退件列表) 
AA[OMS退件] --> B 
AAA[WMS退件预报] --> B 
    B --> C{条件a}
    C --> |a=1| D[结果1]
    C --> |a=2| E[结果2]
    F[竖向流程图]
```
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