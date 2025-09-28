package com.lpy.service;


import com.lpy.entity.Article;
import com.lpy.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ElasticsearchRestTemplate elasticsearchTemplate;

    /**
     * 保存文章
     */
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    /**
     * 批量保存文章
     */
    public Iterable<Article> saveAll(List<Article> articles) {
        return articleRepository.saveAll(articles);
    }

    /**
     * 根据ID查询文章
     */
    public Article findById(String id) {
        return articleRepository.findById(id).orElse(null);
    }

    /**
     * 查询所有文章
     */
    public Iterable<Article> findAll() {
        return articleRepository.findAll();
    }

    /**
     * 删除文章
     */
    public void deleteById(String id) {
        articleRepository.deleteById(id);
    }

    /**
     * 根据标题搜索（精确匹配）
     */
    public List<Article> findByTitle(String title) {
        return articleRepository.findByTitle(title);
    }

    /**
     * 根据作者查询
     */
    public List<Article> findByAuthor(String author) {
        return articleRepository.findByAuthor(author);
    }

    /**
     * 关键词搜索（标题或内容）
     */
    public Page<Article> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findByTitleOrContent(keyword, pageable);
    }

    /**
     * 多字段搜索
     */
    public Page<Article> multiFieldSearch(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.searchByMultiFields(keyword, pageable);
    }

    /**
     * 高级搜索（带高亮显示）
     */
    public List<Article> advancedSearch(String keyword, int page, int size) {
        // 构建查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title", keyword).boost(2.0f))
                .should(QueryBuilders.matchQuery("content", keyword))
                .should(QueryBuilders.matchQuery("tags", keyword));

        // 构建高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("title")
                .field("content")
                .preTags("<em style='color:red'>")
                .postTags("</em>")
                .fragmentSize(100)
                .numOfFragments(3);

        // 构建查询对象
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withHighlightBuilder(highlightBuilder)
                .withPageable(PageRequest.of(page, size))
                .build();

        // 执行搜索
        SearchHits<Article> searchHits = elasticsearchTemplate.search(searchQuery, Article.class);

        // 处理高亮结果
        return searchHits.stream().map(hit -> {
            Article article = hit.getContent();
            Map<String, List<String>> highlightFields = hit.getHighlightFields();

            // 处理高亮标题
            if (highlightFields.containsKey("title")) {
                List<String> titleHighlights = highlightFields.get("title");
                if (!titleHighlights.isEmpty()) {
                    article.setTitle(titleHighlights.get(0));
                }
            }

            // 处理高亮内容
            if (highlightFields.containsKey("content")) {
                List<String> contentHighlights = highlightFields.get("content");
                if (!contentHighlights.isEmpty()) {
                    article.setContent(String.join("...", contentHighlights));
                }
            }

            return article;
        }).collect(Collectors.toList());
    }

    /**
     * 复合查询（关键词 + 分类 + 作者）
     */
    public List<Article> complexSearch(String keyword, String category, String author, int page, int size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 关键词查询
        if (keyword != null && !keyword.trim().isEmpty()) {
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("title", keyword).boost(2.0f))
                    .should(QueryBuilders.matchQuery("content", keyword))
                    .should(QueryBuilders.matchQuery("tags", keyword));
            boolQuery.must(keywordQuery);
        }

        // 分类过滤
        if (category != null && !category.trim().isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("category", category));
        }

        // 作者过滤
        if (author != null && !author.trim().isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("author", author));
        }

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<Article> searchHits = elasticsearchTemplate.search(searchQuery, Article.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
