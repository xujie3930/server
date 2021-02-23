package com.szmsd.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szmsd.system.api.domain.SysSite;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 网点树结构
 */
@Data
public class TreeSelectCode implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 节点ID
     */
    private Long id;

    /**
     * 节点名称
     */
    private String label;
    /**
     * 节点名称
     */
    private String code;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelectCode> children;

    public TreeSelectCode(SysSite sysSite) {
        this.id = sysSite.getId();
        this.label = sysSite.getSiteNameChinese();
        this.code = sysSite.getSiteCode();
        this.children = sysSite.getChildren().stream().map(TreeSelectCode::new).collect(Collectors.toList());
    }

}
