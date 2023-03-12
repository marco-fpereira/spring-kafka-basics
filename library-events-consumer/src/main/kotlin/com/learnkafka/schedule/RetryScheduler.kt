package com.learnkafka.schedule

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RetryScheduler {

    @Scheduled(fixedRate = 1000)
    fun retryFailedRecords() {
        TODO(
            "If the failed events were sent to a database, for example, " +
                    "then it's possible to use this method to get these messages " +
                    "from the database and retry'em in to the main flow")
    }
}