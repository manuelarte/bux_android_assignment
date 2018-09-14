package org.manuel.buxassignment.websocket

import org.manuel.buxassignment.websocket.domain.TradingEvent

interface BuxWebSocketHandler {

    fun onTradingEvent(tradingEvent: TradingEvent<*>)

}