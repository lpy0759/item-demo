*
Maven运行测试命令:

# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest

# 运行特定测试方法
mvn test -Dtest=UserServiceTest#testSaveUser_Success

# 跳过测试
mvn install -DskipTests

# 运行集成测试
mvn verify

# 生成测试报告
mvn surefire-report:report

# 查看测试覆盖率(需要jacoco插件)
mvn jacoco:report

测试最佳实践:
1. 遵循AAA模式: Arrange(准备) -> Act(执行) -> Assert(断言)
2. 每个测试方法只测试一个场景
3. 测试方法名要清晰表达测试意图
4. 使用@BeforeEach和@AfterEach管理测试数据
5. Mock外部依赖，只测试当前组件
6. 集成测试使用@Transactional确保数据隔离
7. 参数化测试减少重复代码
8. 使用TestContainers进行真实数据库测试
9. 编写性能测试监控系统性能
10. 保持测试的独立性和可重复性

目录结构:
src/
├── main/
│   └── java/
│       └── com/example/demo/
│           ├── DemoApplication.java
│           ├── controller/
│           ├── service/
│           ├── repository/
│           └── entity/
└── test/
    ├── java/
    │   └── com/example/demo/
    │       ├── controller/
    │       ├── service/
    │       ├── repository/
    │       ├── integration/
    │       ├── performance/
    │       └── testutil/
    └── resources/
        └── application-test.properties