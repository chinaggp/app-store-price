package com.hypo.appstoreprice.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 手动配置 MyBatis-Plus SqlSessionFactory，
 * 绕过 mybatis-plus-spring-boot3-starter 自动配置与 Spring Boot 4 不兼容的问题。
 *
 * Spring Boot 4 把 DataSourceAutoConfiguration 的包路径从
 * org.springframework.boot.autoconfigure.jdbc 移到了
 * org.springframework.boot.jdbc.autoconfigure，
 * 而 mybatis-plus 3.5.x 的自动配置类还在引用旧路径，导致 ClassNotFoundException。
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 仅在 mapper XML 目录存在时加载
        try {
            org.springframework.core.io.Resource[] resources =
                    new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml");
            if (resources.length > 0) {
                factoryBean.setMapperLocations(resources);
            }
        } catch (java.io.FileNotFoundException ignored) {
            // mapper 目录不存在，跳过
        }

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        factoryBean.setConfiguration(configuration);

        return factoryBean.getObject();
    }
}
