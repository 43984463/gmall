package com.sherlock.gmall.order.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @auther Sherlock
 * @date 2020/9/6 23:03
 * @Description:
 */
@Configuration
public class MySeataConfig {

    /**
     * 雷神版本 但是我的不行  修改为下面的
     * @param dataSourceProperties
     * @return
     */
    /*@Bean
    public DataSource datasource(DataSourceProperties dataSourceProperties){
        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if (StringUtils.hasText(dataSourceProperties.getName())) {
            dataSource.setPoolName(dataSourceProperties.getName());
        }
        return new DataSourceProxy(dataSource);
    }*/


    /**
     * @see MybatisPlusAutoConfiguration#sqlSessionFactory(javax.sql.DataSource)
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        bean.setMapperLocations(resolver.getResources("classpath*:mapper/**/*.xml"));
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig().setIdType(IdType.AUTO);
        bean.setGlobalConfig(new GlobalConfig().setDbConfig(dbConfig));

        SqlSessionFactory factory = null;
        try {
            factory = bean.getObject();
            factory.getConfiguration().setUseGeneratedKeys(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return factory;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
