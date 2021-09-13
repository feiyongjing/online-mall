package com.github.eric.mall.service;

import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.vo.CategoryVo;
import com.github.eric.mall.vo.ResponseVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class CategotyServiceTest extends AbstractUnitTest {

    @Autowired
    CategotyService categotyService;

    @Test
    void selectAll() {
        List<CategoryVo> level_1_CategoryList = new ArrayList<>();
        List<CategoryVo> level_2_CategoryList = new ArrayList<>();
        List<CategoryVo> level_3_CategoryList = new ArrayList<>();
        List<CategoryVo> level_4_CategoryList = new ArrayList<>();

        level_1_CategoryList.add(new CategoryVo(100001, 0, "家用电器", true, 5));
        level_1_CategoryList.add(new CategoryVo(100002, 0, "数码3C", true, 3));
        level_1_CategoryList.add(new CategoryVo(100003, 0, "服装箱包", true, 4));
        level_1_CategoryList.add(new CategoryVo(100004, 0, "食品生鲜", true, 1));
        level_1_CategoryList.add(new CategoryVo(100005, 0, "酒水饮料", true, 2));

        level_2_CategoryList.add(new CategoryVo(100006, 100001, "冰箱", true, 1));
        level_2_CategoryList.add(new CategoryVo(100007, 100001, "电视", true, 1));
        level_2_CategoryList.add(new CategoryVo(100008, 100001, "洗衣机", true, 1));
        level_2_CategoryList.add(new CategoryVo(100009, 100001, "空调", true, 1));
        level_2_CategoryList.add(new CategoryVo(100010, 100001, "电热水器", true, 1));
        level_2_CategoryList.add(new CategoryVo(100011, 100002, "电脑", true, 1));
        level_2_CategoryList.add(new CategoryVo(100012, 100002, "手机", true, 1));
        level_2_CategoryList.add(new CategoryVo(100013, 100002, "平板电脑", true, 1));
        level_2_CategoryList.add(new CategoryVo(100014, 100002, "数码相机", true, 1));
        level_2_CategoryList.add(new CategoryVo(100015, 100002, "3C配件", true, 1));

        level_3_CategoryList.add(new CategoryVo(100016, 100006, "国产冰箱", true, 1));
        level_3_CategoryList.add(new CategoryVo(100017, 100006, "进口冰箱", true, 1));

        level_4_CategoryList.add(new CategoryVo(100018, 100016, "家用冰箱", true, 1));
        level_4_CategoryList.add(new CategoryVo(100019, 100016, "十字冰箱", true, 1));
        level_4_CategoryList.add(new CategoryVo(100020, 100017, "法士冰箱", true, 1));
        level_4_CategoryList.add(new CategoryVo(100021, 100017, "自由嵌入冰箱", true, 1));

        ResponseVo<List<CategoryVo>> responseVo = categotyService.selectAll();
        List<CategoryVo> level_1_CategoryInDbList = responseVo.getData();
        List<CategoryVo> level_2_CategoryInDbList = getNextLevelList(level_1_CategoryInDbList);
        List<CategoryVo> level_3_CategoryInDbList = getNextLevelList(level_2_CategoryInDbList);
        List<CategoryVo> level_4_CategoryInDbList = getNextLevelList(level_3_CategoryInDbList);

        assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
        verify(level_1_CategoryList, level_1_CategoryInDbList);
        verify(level_2_CategoryList, level_2_CategoryInDbList);
        verify(level_3_CategoryList, level_3_CategoryInDbList);
        verify(level_4_CategoryList, level_4_CategoryInDbList);

    }

    private void verify(List<CategoryVo> categoryVoList, List<CategoryVo> categoryVoInDbList) {
        categoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
        verifyData(categoryVoList, categoryVoInDbList, CategoryVo::getId);
        verifyData(categoryVoList, categoryVoInDbList, CategoryVo::getParentId);
        verifyData(categoryVoList, categoryVoInDbList, CategoryVo::getName);
        verifyData(categoryVoList, categoryVoInDbList, CategoryVo::getStatus);
        verifyData(categoryVoList, categoryVoInDbList, CategoryVo::getSortOrder);

    }

    private void verifyData(List<CategoryVo> categoryVoList, List<CategoryVo> categoryVoInDbList, Function<? super CategoryVo, ?> mapper) {
        assertEquals(categoryVoList.stream().map(mapper).collect(Collectors.toList()),
                categoryVoInDbList.stream().map(mapper).collect(Collectors.toList()));
    }


    private List<CategoryVo> getNextLevelList(List<CategoryVo> root) {
        return root.stream()
                .map(CategoryVo::getSubCategories)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Test
    void getCategoryIdSet() {
        Set<Integer> categoryIdSet = new HashSet<>(Arrays.asList(100001,
                100006, 100007, 100008, 100009, 100010,
                100016, 100017, 100018, 100019, 100020, 100021
        ));
        Set<Integer> categoryIdInDbSet = categotyService.getCategoryIdSet(100001);
        assertEquals(categoryIdSet,categoryIdInDbSet);
    }
}