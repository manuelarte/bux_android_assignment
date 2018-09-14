package org.manuel.buxassignment.websocket.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Assert
import org.junit.Test

class TradingEventDeserializeTest {

    private val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()

    @Test
    fun testConnectedEvent() {
        val json = "{\"t\":\"connect.connected\",\"id\":\"1b25b6cb-b824-11e8-a0ac-fd261bcaab71\",\"v\":1,\"body\":{\"sessionId\":\"b739fb80-3575-4b01-8751-33d1a4dc8f92\",\"time\":1536932584855,\"pop\":{\"clientId\":\"8473622939\",\"sessionId\":\"b739fb80-3575-4b01-8751-33d1a4dc8f92\"},\"clientVersion\":\"UNKNOWN\"}}"
        val tradingEvent = objectMapper.readValue<TradingEvent<*>>(json)
        Assert.assertEquals(ConnectedEvent::class, tradingEvent::class)
    }

    @Test
    fun testConnectionFailedEvent() {
        val json = "{\"t\":\"connect.failed\",\"id\":\"a0133723-b823-11e8-a7a7-e703c20e8d02\",\"v\":1,\"body\":{\"developerMessage\":\"Missing JWT Access Token in Authorization header\",\"errorCode\":\"AUTH_009\"}}"

    }

    @Test
    fun testUnknownTradingEvent() {
        val json = "{\"t\":\"portfolio.performance\",\"id\":\"075808f2-b829-11e8-84c1-ab9a748e3619\",\"v\":1,\"body\":{\"accountValue\":{\"currency\":\"BUX\",\"decimals\":2,\"amount\":\"98976.44\"},\"performance\":\"-0.0611\",\"suggestFunding\":false}}"
        val tradingEvent = objectMapper.readValue<TradingEvent<*>>(json)
        Assert.assertEquals(UnknownTradingEvent::class, tradingEvent::class)
    }

}