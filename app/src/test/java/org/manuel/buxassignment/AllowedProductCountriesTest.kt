package org.manuel.buxassignment

import org.junit.Assert
import org.junit.Test

class AllowedProductCountriesTest {

    @Test
    fun getEURCurrencyTest() {
        val eurCurrency = AllowedProductCountries.getCurrencyFromCountryCode("EUR")
        Assert.assertEquals(2, eurCurrency.defaultFractionDigits)
    }

    @Test
    fun getUSDCurrencyTest() {
        val usdCurrency = AllowedProductCountries.getCurrencyFromCountryCode("USD")
        Assert.assertEquals(2, usdCurrency.defaultFractionDigits)
    }
}