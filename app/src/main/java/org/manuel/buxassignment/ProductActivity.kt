package org.manuel.buxassignment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
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


const val PRODUCT_ID = "org.manuel.buxassignment.PRODUCT_ID"

class ProductActivity : AppCompatActivity() {

    private val mObjectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    private lateinit var mContentLayout: View
    private lateinit var mLoadingLayout: View
    private lateinit var mProductDetail: ProductDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val productId = intent.getStringExtra(PRODUCT_ID)
        setContentView(R.layout.activity_product)
        setSupportActionBar(toolbar)

        mContentLayout = findViewById(R.id.content_product)
        mLoadingLayout = findViewById(R.id.loading)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        mContentLayout.visibility = View.GONE
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.beta.getbux.com/core/21/")
                .addConverterFactory(JacksonConverterFactory.create(mObjectMapper))
                .build()

        val service = retrofit.create<ProductService>(ProductService::class.java)
        val productCall = service.getProduct(productId)
        productCall.enqueue(OnProductDetailLoaded(this))
    }

    fun onProductLoaded(productDetail: ProductDetail) {
        mProductDetail = productDetail
        mContentLayout.visibility = View.VISIBLE
        mLoadingLayout.visibility = View.GONE

        val productTitle = findViewById<TextView>(R.id.product_title)
        productTitle.text = productDetail.displayName
        onPriceUpdated(productDetail.currentPrice)
    }

    fun onPriceUpdated(currentPrice: Price) {
        val productCurrentPriceValue = findViewById<TextView>(R.id.product_current_price_value)
        productCurrentPriceValue.text = currentPrice.amount.toString() + currentPrice.currency

        val productDifferencePriceValue = findViewById<TextView>(R.id.product_difference_price_value)
        productDifferencePriceValue.text = getPercentage(currentPrice.amount) + "%"
    }

    private fun getPercentage(currentPrice: BigDecimal): String {
        val difference = currentPrice.divide(mProductDetail.closingPrice.amount, 4, RoundingMode.HALF_UP)
        return (difference.minus(BigDecimal.ONE)).times(BigDecimal.valueOf(100)).toString()
    }

}

class OnProductDetailLoaded(private val activity: ProductActivity) : Callback<ProductDetail> {

    override fun onFailure(call: Call<ProductDetail>, t: Throwable) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResponse(call: Call<ProductDetail>, response: Response<ProductDetail>) {
        activity.onProductLoaded(response.body()!!)
    }

}
