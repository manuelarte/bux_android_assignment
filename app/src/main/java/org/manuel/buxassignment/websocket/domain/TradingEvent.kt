package org.manuel.buxassignment.websocket.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonSubTypes.Type

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "t", include = JsonTypeInfo.As.PROPERTY, defaultImpl = UnknownTradingEvent::class)
@JsonSubTypes(
    Type(value = ConnectedEvent::class, name = ConnectedEvent.TYPE)
)
interface TradingEvent<T> {

    val id: String

    val v: String

    val t: String

    val body: T
}