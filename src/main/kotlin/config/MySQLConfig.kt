package org.inno.config

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class MySQLConfig(
    @Value("\${database.mysql.url}") val url: String,
    @Value("\${database.mysql.username}") val username: String,
    @Value("\${database.mysql.password}") val password: String,
    @Value("\${database.mysql.driver-class-name}") val driver: String,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun logConfig() {
        logger.info("=== Mysql Setting Info ===")
        logger.info("  - url: $url")
        logger.info("  - username: $username")
        logger.info("  - driver: $driver")
    }

    @Bean
    fun dataSource() : DataSource {
        val dataSource = DriverManagerDataSource(url, username, password)
        dataSource.setDriverClassName(driver)
        return dataSource
    }

    @Bean
    fun transactionManager(dataSource: DataSource) : PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }
}