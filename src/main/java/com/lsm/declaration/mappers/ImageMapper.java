package com.lsm.declaration.mappers;

import com.lsm.declaration.entities.ImageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ImageMapper {
    int insertImage(ImageEntity image);

    ImageEntity selectImageByIndex(@Param("index") int index);
}
