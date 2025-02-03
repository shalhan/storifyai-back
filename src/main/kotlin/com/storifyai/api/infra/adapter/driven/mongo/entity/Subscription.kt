package com.storifyai.api.infra.adapter.driven.mongo.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

data class Subscription(
    @BsonId
    val id: ObjectId = ObjectId(),

    val userId: String,

    val type: String,
    val quota: Number,
    val remainingQuota: Number,

    @CreatedDate
    public val startDate: Instant,
    public val endDate: Instant,

    @LastModifiedDate
    public val lastModifiedDate: Instant,

    public val deletedDate: Instant? = null
)