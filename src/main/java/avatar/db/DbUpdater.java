
package avatar.db;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

public class DbUpdater {

    private static final Logger logger = Logger.getLogger(DbUpdater.class);
    private final QueryRunner queryRunner;

    public DbUpdater(QueryRunner queryRunner) {
        this.queryRunner = queryRunner;
    }

    public int update(String sql, Object... params) {
        try {
            return this.queryRunner.update(sql, params);
        } catch (Exception e) {
            logger.error("update() EXCEPTION: " + e.getMessage());
            return -1;
        }
    }
}
