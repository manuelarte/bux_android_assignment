package org.manuel.buxassignment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.android.synthetic.main.activity_product.*
import org.manuel.buxassignment.domain.Price
import org.manuel.buxassignment.domain.ProductDetail
import org.manuel.buxassignment.domain.events.ConnectedEvent
import org.manuel.buxassignment.domain.events.SubscriptionEvent
import org.manuel.buxassignment.domain.events.TradingQuoteEvent
import org.manuel.buxassignment.services.ProductService
import org.manuel.buxassignment.websocket.BuxWebSocketHandler
import org.manuel.buxassignment.websocket.BuxWebSocketHandlerImpl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat


private const val TAG = "ProductActivity"
const val PRODUCT_ID = "org.manuel.buxassignment.PRODUCT_ID"

class ProductActivity : AppCompatActivity() {

    private val mObjectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    private val mCurrencyFormat = NumberFormat.getNumberInstance()

    private lateinit var mProductId: String
    private lateinit var mContentLayout: View
    private lateinit var mLoadingLayout: View
    private lateinit var mProductDetail: ProductDetail

    private lateinit var mBuxWebSocketHandler: BuxWebSocketHandlerImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mProductId = intent.getStringExtra(PRODUCT_ID)
        setContentView(R.layout.activity_product)
        setSupportActionBar(toolbar)

        mContentLayout = findViewById(R.id.content_product)
        mLoadingLayout = findViewById(R.id.loading)
        mContentLayout.visibility = View.GONE

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.beta.getbux.com/core/21/")
                .addConverterFactory(JacksonConverterFactory.create(mObjectMapper))
                .build()

        val service = retrofit.create<ProductService>(ProductService::class.java)
        service.getProduct(mProductId).enqueue(OnProductDetailLoaded(this))
    }

    fun onProductLoaded(productDetail: ProductDetail) {
        mProductDetail = productDetail
        mContentLayout.visibility = View.VISIBLE
        mLoadingLayout.visibility = View.GONE

        val productTitle = findViewById<TextView>(R.id.product_title)
        productTitle.text = productDetail.displayName
        onPriceUpdated(productDetail.currentPrice.amount)

        mBuxWebSocketHandler = BuxWebSocketHandlerImpl(this)
    }

    fun onConnectedEvent(connectedEvent: ConnectedEvent) {
        mBuxWebSocketHandler.send(createSubscriptionEvent())
    }

    fun onTradingQuoteEvent(tradingQuoteEvent: TradingQuoteEvent) {
        if (tradingQuoteEvent.body.securityId == mProductId) {
            this.runOnUiThread {onPriceUpdated(tradingQuoteEvent.body.currentPrice)}
        }
    }

    fun onProductLoadedFail() {
        val intent = Intent(this, ProductDetailErrorActivity::class.java).apply {
            putExtra(PRODUCT_ID, mProductId)
        }
        startActivity(intent)
    }

    private fun onPriceUpdated(currentPrice: BigDecimal) {
        val productCurrentPriceValue = findViewById<TextView>(R.id.product_current_price_value)
        productCurrentPriceValue.text = mCurrencyFormat.format(currentPrice) +
                AllowedProductCountries.getCurrencyFromCountryCode(mProductDetail.currentPrice.currency).symbol

        val productDifferencePriceValue = findViewById<TextView>(R.id.product_difference_price_value)
        productDifferencePriceValue.text = NumberFormat.getPercentInstance().format(getPercentage(currentPrice))
    }

    private fun getPercentage(currentPrice: BigDecimal): Number {
        val difference = currentPrice.divide(mProductDetail.closingPrice.amount, 4, RoundingMode.HALF_UP)
        return difference.minus(BigDecimal.ONE)
    }

    private fun createSubscriptionEvent(): SubscriptionEvent {
        return SubscriptionEvent(arrayOf("trading.product.$mProductId"))
    }

    override fun onBackPressed() {
        mBuxWebSocketHandler.close()
        super.onBackPressed()
    }

}

class OnProductDetailLoaded(private val activity: ProductActivity) : Callback<ProductDetail> {

    override fun onFailure(call: Call<ProductDetail>, t: Throwable) {
        Log.v(TAG, "Error loading Product Details")
        activity.onProductLoadedFail()
    }

    override fun onResponse(call: Call<ProductDetail>, response: Response<ProductDetail>) {
        activity.onProductLoaded(response.body()!!)
    }

}
