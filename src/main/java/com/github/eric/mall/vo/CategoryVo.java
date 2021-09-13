package com.github.eric.mall.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private List<CategoryVo> subCategories;

    public CategoryVo() {
    }

    public CategoryVo(Integer id) {
        this.id = id;
    }

    public CategoryVo(Integer id, Integer parentId, String name, Boolean status, Integer sortOrder) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.status = status;
        this.sortOrder = sortOrder;
    }
}
