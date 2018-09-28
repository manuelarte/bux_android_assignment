package org.manuel.buxassignment.domain.events

class SubscriptionEvent(val subscribeTo: Array<String>, val unsubscribeFrom: Array<String> = emptyArray())