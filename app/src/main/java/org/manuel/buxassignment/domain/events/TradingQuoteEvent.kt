package org.manuel.buxassignment.domain.events

import java.math.BigDecimal

class TradingQuoteEvent(override val id: String, override val v: String, override val body: TradingQuoteEventBody) : TradingEvent<TradingQuoteEventBody> {

    companion object {
        const val TYPE = "trading.quote"
    }

    override val t: String get() = TYPE

}

class TradingQuoteEventBody(val securityId: String, val currentPrice: BigDecimal)