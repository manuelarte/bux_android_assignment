package org.manuel.buxassignment.domain.events

class UnknownTradingEvent(override val id: String, override val v: String, override val body: Map<String, Object>) : TradingEvent<Map<String, Object>> {

    override val t: String
        get() = "Unknown"
}