package org.manuel.buxassignment.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.manuel.buxassignment.websocket.domain.TradingEvent

private const val NORMAL_CLOSURE_STATUS = 1000

class BuxWebSocketListener(private val handler: BuxWebSocketHandler) : WebSocketListener() {

    private val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        //webSocket.send("Hello, it's Manuel !")
        //webSocket.send("What's up ?")
        //webSocket.send(ByteString.decodeHex("deadbeef"))
        //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        if (text != null) {
            val tradingEvent = this.objectMapper.readValue<TradingEvent<*>>(text)
            handler.onTradingEvent(tradingEvent)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        //output("Receiving bytes : " + bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        //output("Closing : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
       //output("Error : " + t.message)
    }

}