>版本更新历史
---
## V21-11-24

##### 实现客户报价功能，在匹配不到客户的具体报价的情况下，根据客户所属的类型，去匹配报价。
1.  用户添加报价用户类型
2.  业务费用规则、仓租费规则，可以支持绑定用户类型/指定用户id + 折扣
    * 优先取用户报价，没有就取用户类型的规则，没有就取全局