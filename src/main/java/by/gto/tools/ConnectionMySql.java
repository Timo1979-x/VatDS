package by.gto.tools;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.sql.SQLException;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Aleks
 */
public class ConnectionMySql {

    private static volatile ConnectionMySql instance;
    // private ConfigReader config = new ConfigReader();
    private static PooledConnection pool = null;
    //  private Connection conn = null;
    private static final Logger log = LogManager.getLogger(ConnectionMySql.class);

    public static ConnectionMySql getInstance() {
        if (instance == null) {
            synchronized (ConnectionMySql.class) {
                if (instance == null) {
                    instance = new ConnectionMySql();
                }
            }
        }
        return instance;
    }

    private ConnectionMySql() {
    }

//    public void onnectionMySql() {
//        MysqlConnectionPoolDataSource cpMySql = new MysqlConnectionPoolDataSource();
//        cpMySql.setURL("jdbc:mysql://" + ConfigReader.getHost() + ":" + ConfigReader.getPort() + "/to");
//        cpMySql.setUser("root");
//        cpMySql.setPassword("ghbdfnbpfwbz");
//        ConnectionPoolDataSource cp = cpMySql;
//        try {
//            pool = cp.getPooledConnection();
//        } catch (RuntimeException ex) {
//            log.error("RuntimeException ", ex);
//        } catch (SQLException ex) {
//            log.fatal("SQLException", ex);
//        }
//    }
    public Connection getConn() {
        try {
            if (pool == null) {
                MysqlConnectionPoolDataSource cpMySql = new MysqlConnectionPoolDataSource();
                cpMySql.setURL(String.format("jdbc:mysql://%s:%s/to", ConfigReader.getInstance().getHost(), ConfigReader.getInstance().getPort()));
                cpMySql.setUser("root");
                cpMySql.setPassword("ghbdfnbpfwbz");
                cpMySql.setConnectTimeout(28800);
                cpMySql.setCharacterSetResults("utf8");
                cpMySql.setUseCompression(true);
                       
                ConnectionPoolDataSource cp = cpMySql;
                try {
                    pool = cp.getPooledConnection();
                } catch (RuntimeException ex) {
                    log.error("RuntimeException ", ex);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка подключения к серверу.", "Ошибка", 0);
                    log.fatal("SQLException", ex);
                }
            }
            return (Connection) pool.getConnection();
        } catch (SQLException ex) {
            log.error("!SQLException2 getConn()", ex);
            throw new RuntimeException("Unable to optain connection from connection pool", ex);
        }
    }

//    public static Connection getConn() {
//        try {
//            conn = (Connection) pool.getConnection();
//        } catch (SQLException ex) {
//            log.error("!SQLException2 ", ex);
//        }
//        return conn;
//    }
//    public static void closeConn() {
//        if (conn != null) {
//            try {
//                conn.close();
//            } catch (SQLException ex) {
//                log.error("!SQLException3 ", ex);
//            }
//        }
//    }
//
//    public static void destroy() {
//        try {
//            if (conn != null) {
//
//                conn.close();
//            }
//            if (pool != null) {
//
//                pool.close();
//            }
//        } catch (SQLException ex) {
//            log.error("!SQLException4 ", ex);
//        }
//    }
    public void destroy() {
        try {
            if (pool != null) {
                pool.close();
            }
        } catch (SQLException ex) {
            log.error("!SQLException4 destroy()", ex);
        }
    }
}
