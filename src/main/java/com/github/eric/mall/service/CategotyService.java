package com.github.eric.mall.service;

import com.github.eric.mall.consts.OnlineMallConst;
import com.github.eric.mall.dao.MyCategoryMapper;
import com.github.eric.mall.generate.entity.Category;
import com.github.eric.mall.vo.CategoryVo;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategotyService {
    @Autowired
    public MyCategoryMapper myCategoryMapper;


    public ResponseVo<List<CategoryVo>> selectAll() {
        List<CategoryVo> categoryVoList = new ArrayList<>();
        List<Category> categoryInDbs = myCategoryMapper.selectAll();
        categoryInDbs.removeIf(category -> removeAndReturnData(categoryVoList, category, OnlineMallConst.ROOT_PARENT_ID));
        classify(categoryInDbs, categoryVoList);
        return ResponseVo.success(categoryVoList);

    }

    public Set<Integer> getCategoryIdSet(Integer rootParentId) {
        Set<Integer> result = new HashSet<>();
        List<Category> categoryInDbs = myCategoryMapper.selectAll();
        result.add(rootParentId);
        removeAndReturnData(categoryInDbs, result, rootParentId);
        return result;

    }

    private void removeAndReturnData(List<Category> categoryInDbs, Set<Integer> result, Integer parentId) {
        Set<Integer> childIds = new HashSet<>();
        categoryInDbs.removeIf(category -> {
            if (category.getParentId().equals(parentId)) {
                childIds.add(category.getId());
                return true;
            }
            return false;
        });
        result.addAll(childIds);
        childIds.forEach(childId -> removeAndReturnData(categoryInDbs, result, childId));
    }


    private boolean removeAndReturnData(List<CategoryVo> categoryVoList, Category category, Integer parentId) {
        if (category.getParentId().equals(parentId)) {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(category, categoryVo);
            categoryVoList.add(categoryVo);
            return true;
        }
        return false;
    }

    private List<CategoryVo> classify(List<Category> categoryInDbs, List<CategoryVo> categoryVoList) {
        categoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder));
        categoryVoList.forEach(categoryVo -> {
            List<CategoryVo> categoryVoListItem = new ArrayList<>();
            categoryInDbs.removeIf(category -> removeAndReturnData(categoryVoListItem, category, categoryVo.getId()));
            categoryVo.setSubCategories(categoryVoListItem);
        });
        categoryVoList.forEach(categoryVo -> classify(categoryInDbs, categoryVo.getSubCategories()));

        return categoryVoList;
    }


}
