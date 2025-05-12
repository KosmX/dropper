package dev.kosmx.dropper.db

import dev.kosmx.dropper.config.DatabaseAccess
import dev.kosmx.dropper.getLogger
import dev.kosmx.dropper.warn
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object DatabaseLoader {

    val logger by getLogger()

    fun loadDB(config: DatabaseAccess) {

        val isSQLite = config.driver == "org.sqlite.JDBC"
        if (isSQLite) {
            logger.warn { "SQLite database backend is not recommended in production, consider using a proper database like MariaDB" }
        }

        TransactionManager.defaultDatabase =
            Database.connect(config.address, config.driver, config.username, config.password).also {
                transaction(it) {
                    SchemaUtils.createMissingTablesAndColumns(
                        AdminTable,
                        SessionTable,
                        UploadTable,
                        FileTable,
                    )
                }
            }


        if (isSQLite) {
            TransactionManager.manager.defaultIsolationLevel =
                Connection.TRANSACTION_SERIALIZABLE
        }
    }
}