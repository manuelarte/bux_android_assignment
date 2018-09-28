package org.manuel.buxassignment.websocket

import org.manuel.buxassignment.domain.events.TradingEvent

interface BuxWebSocketHandler {

    fun onTradingEvent(tradingEvent: TradingEvent<*>)

}