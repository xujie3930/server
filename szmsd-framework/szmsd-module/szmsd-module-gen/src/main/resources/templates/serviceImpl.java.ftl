package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import java.util.List;

/**
* <p>
    * ${table.comment!} 服务实现类
    * </p>
*
* @author ${author}
* @since ${date}
*/
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {


        /**
        * 查询${table.comment!}模块
        *
        * @param id ${table.comment!}模块ID
        * @return ${table.comment!}模块
        */
        @Override
        public ${entity} select${entity}ById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询${table.comment!}模块列表
        *
        * @param ${entity?uncap_first} ${table.comment!}模块
        * @return ${table.comment!}模块
        */
        @Override
        public List<${entity}> select${entity}List(${entity} ${entity?uncap_first})
        {
        QueryWrapper<${entity}> where = new QueryWrapper<${entity}>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增${table.comment!}模块
        *
        * @param ${entity?uncap_first} ${table.comment!}模块
        * @return 结果
        */
        @Override
        public int insert${entity}(${entity} ${entity?uncap_first})
        {
        return baseMapper.insert(${entity?uncap_first});
        }

        /**
        * 修改${table.comment!}模块
        *
        * @param ${entity?uncap_first} ${table.comment!}模块
        * @return 结果
        */
        @Override
        public int update${entity}(${entity} ${entity?uncap_first})
        {
        return baseMapper.updateById(${entity?uncap_first});
        }

        /**
        * 批量删除${table.comment!}模块
        *
        * @param ids 需要删除的${table.comment!}模块ID
        * @return 结果
        */
        @Override
        public int delete${entity}ByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除${table.comment!}模块信息
        *
        * @param id ${table.comment!}模块ID
        * @return 结果
        */
        @Override
        public int delete${entity}ById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

    </#if>
