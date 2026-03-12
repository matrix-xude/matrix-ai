import com.matrix.ai.MatrixApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * author : xxd
 * date   : 2026/3/12
 * desc   : MySQL 连接测试
 */
@SpringBootTest(classes = MatrixApplication.class)
public class MySqlConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testConnection() throws SQLException {
        // 打印出当前使用的连接池类型
        System.out.println("数据源类型：" + dataSource.getClass().getName());
        // 获取连接，如果不报错，说明连通了
        Connection connection = dataSource.getConnection();
        System.out.println("连接是否成功：" + (connection != null));
        connection.close();
    }
}
