package org.manuel.buxassignment.websocket.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class ConnectedEvent(override val id: String, override val v: String, override val body: ConnectedEventBody) : TradingEvent<ConnectedEventBody> {

    companion object {
        const val TYPE = "connect.connected"
    }

    override val t: String get() = TYPE

}

@JsonIgnoreProperties(ignoreUnknown = true)
class ConnectedEventBody(val sessionId: String)