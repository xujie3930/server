package ${package.Service};

import ${package.Entity}.${entity};
import ${superServiceClassPackage};
import java.util.List;

/**
* <p>
    * ${table.comment!} 服务类
    * </p>
*
* @author ${author}
* @since ${date}
*/
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

        /**
        * 查询${table.comment!}模块
        *
        * @param id ${table.comment!}模块ID
        * @return ${table.comment!}模块
        */
        ${entity} select${entity}ById(String id);

        /**
        * 查询${table.comment!}模块列表
        *
        * @param ${entity?uncap_first} ${table.comment!}模块
        * @return ${table.comment!}模块集合
        */
        List<${entity}> select${entity}List(${entity} ${entity?uncap_first});

        /**
        * 新增${table.comment!}模块
        *
        * @param ${entity?uncap_first} ${table.comment!}模块
        * @return 结果
        */
        int insert${entity}(${entity} ${entity?uncap_first});

        /**
        * 修改${table.comment!}模块
        *
        * @param ${entity?uncap_first} ${table.comment!}模块
        * @return 结果
        */
        int update${entity}(${entity} ${entity?uncap_first});

        /**
        * 批量删除${table.comment!}模块
        *
        * @param ids 需要删除的${table.comment!}模块ID
        * @return 结果
        */
        int delete${entity}ByIds(List<String> ids);

        /**
        * 删除${table.comment!}模块信息
        *
        * @param id ${table.comment!}模块ID
        * @return 结果
        */
        int delete${entity}ById(String id);

}

</#if>
