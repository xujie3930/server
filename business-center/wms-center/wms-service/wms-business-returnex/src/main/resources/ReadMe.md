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