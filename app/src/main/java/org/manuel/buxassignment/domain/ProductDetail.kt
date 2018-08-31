package org.manuel.buxassignment.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ProductDetail(val symbol: String, val securityId: String, val displayName: String, val quoteCurrency: String,
                    val displayDecimal: Int, val currentPrice: Price, val closingPrice: Price)

class Price(val currency: String, val decimals: Int, val amount: BigDecimal)