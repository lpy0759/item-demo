package com.lpy.controller;

import com.lpy.entity.Article;
import com.lpy.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 创建文章
     */
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        article.setCreateTime(LocalDateTime.now());
        Article saved = articleService.save(article);
        return ResponseEntity.ok(saved);
    }

    /**
     * 批量创建测试数据
     */
    @PostMapping("/batch")
    public ResponseEntity<String> createBatchArticles() {
        List<Article> articles = List.of(
                new Article("1", "Spring Boot整合Elasticsearch教程",
                        "本文详细介绍了如何在Spring Boot项目中整合Elasticsearch，包括配置、实体类定义、Repository接口等内容。通过实例演示了中文分词的配置和使用。",
                        "张三", "技术教程", 1500, LocalDateTime.now(), "Spring Boot,Elasticsearch,中文分词"),

                new Article("2", "Java微服务架构设计与实践",
                        "微服务架构是当前企业级应用开发的主流架构模式。本文从理论到实践，详细讲解了Java微服务的设计原则、技术选型和最佳实践。",
                        "李四", "架构设计", 2300, LocalDateTime.now(), "Java,微服务,Spring Cloud"),

                new Article("3", "深入理解MySQL索引优化",
                        "数据库性能优化是每个开发者必须掌握的技能。本文深入分析了MySQL索引的原理、类型，以及如何进行索引优化来提升查询性能。",
                        "王五", "数据库", 1800, LocalDateTime.now(), "MySQL,索引,性能优化"),

                new Article("4", "Vue.js前端开发最佳实践",
                        "Vue.js作为流行的前端框架，在企业级项目开发中应用广泛。本文总结了Vue.js开发的最佳实践，包括组件设计、状态管理等。",
                        "赵六", "前端开发", 1200, LocalDateTime.now(), "Vue.js,前端,JavaScript"),

                new Article("5", "Docker容器化部署指南",
                        "容器化技术已成为现代应用部署的标准方案。本文介绍了Docker的基本概念、镜像制作、容器编排等核心技术。",
                        "钱七", "运维部署", 1600, LocalDateTime.now(), "Docker,容器化,部署")
        );

        articleService.saveAll(articles);
        return ResponseEntity.ok("批量创建成功，共创建 " + articles.size() + " 篇文章");
    }

    /**
     * 根据ID查询文章
     */
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable String id) {
        Article article = articleService.findById(id);
        if (article != null) {
            return ResponseEntity.ok(article);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 查询所有文章
     */
    @GetMapping
    public ResponseEntity<Iterable<Article>> getAllArticles() {
        return ResponseEntity.ok(articleService.findAll());
    }

    /**
     * 根据标题查询
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<List<Article>> getByTitle(@PathVariable String title) {
        return ResponseEntity.ok(articleService.findByTitle(title));
    }

    /**
     * 根据作者查询
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Article>> getByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(articleService.findByAuthor(author));
    }

    /**
     * 关键词搜索
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Article>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.search(keyword, page, size));
    }

    /**
     * 多字段搜索
     */
    @GetMapping("/multi-search")
    public ResponseEntity<Page<Article>> multiSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.multiFieldSearch(keyword, page, size));
    }

    /**
     * 高级搜索（带高亮）
     */
    @GetMapping("/advanced-search")
    public ResponseEntity<List<Article>> advancedSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.advancedSearch(keyword, page, size));
    }

    /**
     * 复合查询
     */
    @GetMapping("/complex-search")
    public ResponseEntity<List<Article>> complexSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.complexSearch(keyword, category, author, page, size));
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable String id) {
        articleService.deleteById(id);
        return ResponseEntity.ok("删除成功");
    }
}
