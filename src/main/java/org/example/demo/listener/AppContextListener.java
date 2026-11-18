package org.example.demo.listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.example.demo.dao.UserDAO;
import org.example.demo.mapper.UserMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@WebListener
public class AppContextListener implements ServletContextListener {
    private HikariDataSource ds;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties props = loadDataSourceProperties();
        HikariConfig config = new HikariConfig(props);
        this.ds = new HikariDataSource(config);

        SqlSessionFactory sqlSessionFactory = buildSqlSessionFactory(this.ds);

        ServletContext ctx = sce.getServletContext();
        ctx.setAttribute("dataSource", this.ds);
        ctx.setAttribute("sqlSessionFactory", sqlSessionFactory);
        ctx.setAttribute("userDAO", new UserDAO(sqlSessionFactory));

        log.info("Application context initialized with MyBatis and DataSource: {}", config.getJdbcUrl());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (this.ds != null) {
            this.ds.close();
            log.info("HikariDataSource closed");
        }
    }

    private SqlSessionFactory buildSqlSessionFactory(HikariDataSource dataSource) {
        Environment environment = new Environment("default", new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.setLogImpl(Slf4jImpl.class);
        configuration.addMapper(UserMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    private Properties loadDataSourceProperties() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found in classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        String envUrl = System.getenv("DB_URL");
        if (envUrl != null && !envUrl.isBlank()) {
            props.setProperty("jdbcUrl", envUrl);
        }
        String envUser = System.getenv("DB_USER");
        if (envUser != null && !envUser.isBlank()) {
            props.setProperty("username", envUser);
        }
        String envPassword = System.getenv("DB_PASSWORD");
        if (envPassword != null) {
            props.setProperty("password", envPassword);
        }
        return props;
    }
}
