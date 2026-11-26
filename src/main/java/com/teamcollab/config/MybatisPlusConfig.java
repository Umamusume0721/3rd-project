package com.teamcollab.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
@org.springframework.context.annotation.Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件：不加这个，selectPage 返回的总条数恒为 0。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setMaxLimit(100L);  // 单页上限，防止前端传 pageSize=100000
        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
