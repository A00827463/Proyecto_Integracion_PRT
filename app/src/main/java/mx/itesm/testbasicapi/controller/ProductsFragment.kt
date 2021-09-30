package mx.itesm.testbasicapi.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import mx.itesm.testbasicapi.R
import mx.itesm.testbasicapi.Utils
import mx.itesm.testbasicapi.Utils.Companion.PRODUCT_ID_KEY
import mx.itesm.testbasicapi.model.Model
import mx.itesm.testbasicapi.model.entities.Product
import mx.itesm.testbasicapi.model.repository.responseinterface.IGetProducts


class ProductsFragment : Fragment() {
    private lateinit var model: Model
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = Model(Utils.getToken(requireContext()))
        showProducts()
        assignAddButtonListener()
    }

    private fun assignAddButtonListener() {
        requireView().findViewById<FloatingActionButton>(R.id.floatBtnAddProduct)
            .setOnClickListener {
                val addProductFragment = AddProductFragment()
                val fragmentTransaction = parentFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.home_content, addProductFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
    }

    private fun showProducts() {
        model.getProducts(object : IGetProducts {
            override fun onSuccess(products: List<Product>?) {
                if (products != null) {
                    val rvProducts = requireView().findViewById<RecyclerView>(R.id.rvProducts)
                    val adapter =
                        ProductsAdapter(products, object : ProductsAdapter.OnItemClickListener {
                            override fun onItemClick(item: Product) {
                                // Toast.makeText(requireContext(), "Clicked on an Item", Toast.LENGTH_SHORT).show()
                                val updateProductFragment = UpdateProductFragment()
                                val bundle = Bundle()
                                bundle.putString(PRODUCT_ID_KEY, item.id)
                                updateProductFragment.arguments = bundle

                                val fragmentTransaction = parentFragmentManager.beginTransaction()
                                fragmentTransaction.replace(
                                    R.id.home_content,
                                    updateProductFragment
                                )
                                fragmentTransaction.addToBackStack(null)
                                fragmentTransaction.commit()
                            }
                        }, Utils.getToken(requireContext()), requireContext())
                    rvProducts.adapter = adapter
                    rvProducts.layoutManager = LinearLayoutManager(requireContext())
                }
            }

            override fun onNoSuccess(code: Int, message: String) {
                Toast.makeText(
                    requireContext(),
                    "Problem detected $code $message",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("getProducts", "$code: $message")
            }

            override fun onFailure(t: Throwable) {
                Toast.makeText(
                    context,
                    "Network or server error occurred",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("addUser", t.message.toString())
            }
        })
    }
}