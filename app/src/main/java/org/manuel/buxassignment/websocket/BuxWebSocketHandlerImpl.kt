package org.manuel.buxassignment.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.manuel.buxassignment.ProductActivity
import org.manuel.buxassignment.domain.events.ConnectedEvent
import org.manuel.buxassignment.domain.events.TradingEvent
import org.manuel.buxassignment.domain.events.TradingQuoteEvent

class BuxWebSocketHandlerImpl(val activity: ProductActivity) : BuxWebSocketHandler {

    private val mClient = OkHttpClient()
    private val mObjectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    private var mConnected = false

    private lateinit var mWs: WebSocket

    init {
        startWebsocket()
    }

    override fun onTradingEvent(tradingEvent: TradingEvent<*>) {
        if (!mConnected) {
            if (tradingEvent::class == ConnectedEvent::class) {
                mConnected = true
                activity.onConnectedEvent(tradingEvent as ConnectedEvent)
            }
        } else {
            if (tradingEvent::class == TradingQuoteEvent::class) {
                activity.onTradingQuoteEvent(tradingEvent as TradingQuoteEvent)
            }
        }
    }

    fun send(any: Any) {
        mWs.send(mObjectMapper.writeValueAsString(any))
    }

    fun close() {
        mWs.close(1000, null)
    }

    private fun startWebsocket() {
        val request = Request.Builder().url("https://rtf.beta.getbux.com/subscriptions/me")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoYWJsZSI6ZmFsc2UsInN1YiI6ImJiMGNkYTJiLWExMGUtNGVkMy1hZDVhLTBmODJiNGMxNTJjNCIsImF1ZCI6ImJldGEuZ2V0YnV4LmNvbSIsInNjcCI6WyJhcHA6bG9naW4iLCJydGY6bG9naW4iXSwiZXhwIjoxODIwODQ5Mjc5LCJpYXQiOjE1MDU0ODkyNzksImp0aSI6ImI3MzlmYjgwLTM1NzUtNGIwMS04NzUxLTMzZDFhNGRjOGY5MiIsImNpZCI6Ijg0NzM2MjI5MzkifQ.M5oANIi2nBtSfIfhyUMqJnex-JYg6Sm92KPYaUL9GKg")
                .addHeader("Accept-Language", "nl-NL,en;q=0.8")
                .build()
        val listener = BuxWebSocketListener(this)
        mWs = mClient.newWebSocket(request, listener)
        mClient.dispatcher().executorService().shutdown()
    }

}