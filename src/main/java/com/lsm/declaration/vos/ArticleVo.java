package com.lsm.declaration.vos;

import com.lsm.declaration.entities.ArticleEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleVo extends ArticleEntity {
    private int commentCount;
}
