package org.manuel.buxassignment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter

private val TAG = "MainActivity"
private val PRODUCTS = arrayOf(ProductItem("Germany30", "sb26493"), ProductItem("US500", "sb26496"),
        ProductItem("EUR/USD", "sb26502"), ProductItem("Gold", "sb26500"), ProductItem("Apple", "sb26513"),
        ProductItem("Deutsche Bank","sb28248"))

class MainActivity : AppCompatActivity(), ProductListFragment.OnListFragmentInteractionListener {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val adapter = ArrayAdapter<ProductItem>(this,
                android.R.layout.simple_dropdown_item_1line, PRODUCTS)
        val productList = findViewById<AutoCompleteTextView>(R.id.product_list)
        productList.setAdapter<ArrayAdapter<ProductItem>>(adapter)
        productList.onItemClickListener = OnProductSelected(this)

    }

    fun onProductSelected(productItem: ProductItem ) {
        val intent = Intent(this, ProductActivity::class.java).apply {
            putExtra(PRODUCT_ID, productItem.productId)
        }
        startActivity(intent)
    }

    override fun getProducts(): List<ProductItem> {
        return PRODUCTS.toList()
    }

    override fun onListFragmentInteraction(item: ProductItem?) {
        this.onProductSelected(item!!)
    }
}

class ProductItem(val displayName: String, val productId: String) {
    override fun toString(): String {
        return displayName
    }
}

class OnProductSelected(private val activity: MainActivity) : AdapterView.OnItemClickListener {

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
        val productItem = p0!!.getItemAtPosition(index) as ProductItem
        Log.v(TAG, "Product selected: $productItem")
        activity.onProductSelected(productItem)
    }

}
