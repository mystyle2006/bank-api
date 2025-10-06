package org.inno.common.logging

import org.inno.common.exception.CustomException
import org.inno.common.exception.ErrCode
import org.slf4j.*

object Logging {
    fun <T: Any> getLogger(clazz: Class<T>) = LoggerFactory.getLogger(clazz)

    fun <T> logFor(log: Logger, function: (MutableMap<String, Any>) -> T?): T {
        val logInfo = mutableMapOf<String, Any>()

        logInfo["start_at"] = now()

        val result = function.invoke(logInfo)

        logInfo["end_at"] = now()

        log.info(logInfo.toString())

        return result ?: throw CustomException(ErrCode.FAILED_TO_INVOKE_IN_LOGGER)

    }

    private fun now(): Long {
        return System.currentTimeMillis()
    }
}