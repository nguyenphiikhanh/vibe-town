
package avatar.db;

import java.util.Collections;
import java.util.List;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;


public class DbExecutor {

    private static final Logger logger = Logger.getLogger(DbExecutor.class);

    private final QueryRunner queryRunner;

    public DbExecutor(QueryRunner queryRunner) {
        this.queryRunner = queryRunner;
    }

    public <T> List<T> selectResultAsListObj(String sql, Class<T> clazz, Object... params) {
        try {
            ResultSetHandler<List<T>> resultSetHandler = new BeanListHandler(clazz, new BasicRowProcessor(new GenerousBeanProcessor()));
            return this.queryRunner.query(sql, resultSetHandler, params);
        } catch (Exception e) {
            logger.error("selectResultAsListObj() EXCEPTION: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
