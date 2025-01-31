package com.storifyai.api.infra.adapter.driven.mongo.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

data class Project(
    @BsonId
    val id: ObjectId = ObjectId(),

    val userId: String,

    val title: String,

    @CreatedDate
    public val createdDate: Instant,

    @LastModifiedDate
    public val lastModifiedDate: Instant,

    public val deletedDate: Instant? = null
)