package com.lpy.repository;


import com.lpy.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

    // 根据标题查询
    List<Article> findByTitle(String title);

    // 根据作者查询
    List<Article> findByAuthor(String author);

    // 根据分类查询
    List<Article> findByCategory(String category);

    // 标题或内容包含关键字（中文分词搜索）
    @Query("{\"bool\":{\"should\":[{\"match\":{\"title\":\"?0\"}},{\"match\":{\"content\":\"?0\"}}]}}")
    Page<Article> findByTitleOrContent(String keyword, Pageable pageable);

    // 多字段搜索
    @Query("{\"multi_match\":{\"query\":\"?0\",\"fields\":[\"title^2\",\"content\",\"tags\"],\"type\":\"best_fields\"}}")
    Page<Article> searchByMultiFields(String keyword, Pageable pageable);

    // 高亮搜索
    @Query("{\"bool\":{\"should\":[{\"match\":{\"title\":{\"query\":\"?0\",\"boost\":2}}},{\"match\":{\"content\":\"?0\"}},{\"match\":{\"tags\":\"?0\"}}]}}")
    Page<Article> searchWithHighlight(String keyword, Pageable pageable);
}
