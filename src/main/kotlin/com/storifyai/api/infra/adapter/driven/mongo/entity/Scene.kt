package com.storifyai.api.infra.adapter.driven.mongo.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

data class Scene(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userId: String,
    val projectId: String,

    val details: List<SceneDetail>,

    @CreatedDate
    val createdDate: Instant,

    @LastModifiedDate
    val lastModifiedDate: Instant,

    val deletedDate: Instant? = null
)

data class SceneDetail(val number: Int, val imageURL: String, val imageReferenceId: String, val setting: Setting, val prompt: Prompt)
data class Setting(val isFull: Boolean, val background: String)
data class Prompt(val characters: List<String>, val style: String, val background: String, val detail: String)