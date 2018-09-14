package org.manuel.buxassignment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.android.synthetic.main.activity_product.*
import org.manuel.buxassignment.domain.Price
import org.manuel.buxassignment.domain.ProductDetail
import org.manuel.buxassignment.services.ProductService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import android.system.Os.shutdown
import okhttp3.Request
import okhttp3.WebSocket
import org.manuel.buxassignment.websocket.BuxWebSocketListener
import okhttp3.OkHttpClient
import org.manuel.buxassignment.websocket.BuxWebSocketHandler
import org.manuel.buxassignment.websocket.domain.ConnectedEvent
import org.manuel.buxassignment.websocket.domain.TradingEvent


private const val TAG = "ProductActivity"
const val PRODUCT_ID = "org.manuel.buxassignment.PRODUCT_ID"

class ProductActivity : AppCompatActivity(), BuxWebSocketHandler {

    private val mObjectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    private val mCurrencyFormat = NumberFormat.getNumberInstance()

    private lateinit var mClient: OkHttpClient
    private lateinit var mWs: WebSocket

    private lateinit var mProductId: String
    private lateinit var mContentLayout: View
    private lateinit var mLoadingLayout: View
    private lateinit var mProductDetail: ProductDetail

    private var webSocketConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mProductId = intent.getStringExtra(PRODUCT_ID)
        setContentView(R.layout.activity_product)
        setSupportActionBar(toolbar)

        mContentLayout = findViewById(R.id.content_product)
        mLoadingLayout = findViewById(R.id.loading)

        mContentLayout.visibility = View.GONE

        mClient = OkHttpClient()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.beta.getbux.com/core/21/")
                .addConverterFactory(JacksonConverterFactory.create(mObjectMapper))
                .build()

        val service = retrofit.create<ProductService>(ProductService::class.java)
        val productCall = service.getProduct(mProductId)
        productCall.enqueue(OnProductDetailLoaded(this))
    }

    fun onProductLoaded(productDetail: ProductDetail) {
        mProductDetail = productDetail
        mContentLayout.visibility = View.VISIBLE
        mLoadingLayout.visibility = View.GONE

        val productTitle = findViewById<TextView>(R.id.product_title)
        productTitle.text = productDetail.displayName
        onPriceUpdated(productDetail.currentPrice)

        startWebsocket()
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

    override fun onTradingEvent(tradingEvent: TradingEvent<*>) {
        if (tradingEvent::class == ConnectedEvent::class && !webSocketConnected) {
            webSocketConnected = true
            // send subscription event
            // mWs.send()
        }
    }

    fun onProductLoadedFail() {
        val intent = Intent(this, ProductDetailErrorActivity::class.java).apply {
            putExtra(PRODUCT_ID, mProductId)
        }
        startActivity(intent)
    }

    private fun onPriceUpdated(currentPrice: Price) {
        val productCurrentPriceValue = findViewById<TextView>(R.id.product_current_price_value)
        productCurrentPriceValue.text = mCurrencyFormat.format(currentPrice.amount) +
                AllowedProductCountries.getCurrencyFromCountryCode(currentPrice.currency).symbol

        val productDifferencePriceValue = findViewById<TextView>(R.id.product_difference_price_value)
        productDifferencePriceValue.text = NumberFormat.getPercentInstance().format(getPercentage(currentPrice.amount))
    }

    private fun getPercentage(currentPrice: BigDecimal): Number {
        val difference = currentPrice.divide(mProductDetail.closingPrice.amount, 4, RoundingMode.HALF_UP)
        return difference.minus(BigDecimal.ONE)
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
