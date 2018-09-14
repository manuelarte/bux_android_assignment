package org.manuel.buxassignment;

import java.util.*

enum class AllowedProductCountries(val currency: Currency) {

    GERMANY(Currency.getInstance("EUR")),
    NETHERLANDS(Currency.getInstance("EUR")),
    SPAIN(Currency.getInstance("EUR")),
    UK(Currency.getInstance("GBP")),
    USA(Currency.getInstance("USD"));


    companion object {

        fun getAvailableCurrencies(): Set<Currency> {
            return values().map { country -> country.currency }.toSet()
        }

        fun getCurrencyFromCountryCode(currencyCode: String): Currency {
            return getAvailableCurrencies().last { currency -> currency.currencyCode == currencyCode }
        }
    }

}
