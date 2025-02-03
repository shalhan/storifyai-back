package com.storifyai.api.common.enum

data class Detail(
    val price: Number,
    val quota: Number,
    val duration: Number,
)

enum class SubscriptionType {
    FREE, SOLO_MONTHLY, SOLO_YEARLY
}

enum class SubscriptionTypeDetail {
    FREE {
        override fun getDetail() = Detail(0,0, 1)
    },
    SOLO_MONTHLY {
        override fun getDetail() = Detail(19, 2500, 1)
    },
    SOLO_YEARLY {
        override fun getDetail() = Detail(15, 2500, 12)
    };

    abstract fun getDetail(): Detail
}