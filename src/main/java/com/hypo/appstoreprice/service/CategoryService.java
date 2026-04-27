package com.hypo.appstoreprice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hypo.appstoreprice.entity.CategoryEntity;
import com.hypo.appstoreprice.entity.WatchedAppEntity;
import com.hypo.appstoreprice.mapper.CategoryMapper;
import com.hypo.appstoreprice.mapper.WatchedAppMapper;
import com.hypo.appstoreprice.pojo.response.CategoryResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final WatchedAppMapper watchedAppMapper;

    /**
     * 获取分类列表
     */
    public List<CategoryResDTO> getCategoryList() {
        List<CategoryEntity> categoryEntities = categoryMapper.selectList(
            new LambdaQueryWrapper<CategoryEntity>().orderByAsc(CategoryEntity::getSortOrder)
        );

        return categoryEntities.stream().map(entity -> {
            CategoryResDTO dto = new CategoryResDTO();
            dto.setId(entity.getId());
            dto.setName(entity.getName());
            dto.setDescription(entity.getDescription());
            dto.setIcon(entity.getIcon());
            
            // 统计该分类下的 App 数量
            Long count = watchedAppMapper.selectCount(
                new LambdaQueryWrapper<WatchedAppEntity>()
                    .eq(WatchedAppEntity::getCategoryId, entity.getId())
                    .eq(WatchedAppEntity::getEnabled, 1)
            );
            dto.setAppCount(count.intValue());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取分类
     */
    public CategoryEntity getCategoryById(String id) {
        return categoryMapper.selectById(id);
    }
}
