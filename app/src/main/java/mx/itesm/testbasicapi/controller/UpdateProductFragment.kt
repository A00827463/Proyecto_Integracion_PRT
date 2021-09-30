package mx.itesm.testbasicapi.controller

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import mx.itesm.testbasicapi.R
import mx.itesm.testbasicapi.Utils
import mx.itesm.testbasicapi.Utils.Companion.PRODUCT_ID_KEY
import mx.itesm.testbasicapi.model.Model
import mx.itesm.testbasicapi.model.entities.Product
import mx.itesm.testbasicapi.model.repository.RemoteRepository
import mx.itesm.testbasicapi.model.repository.responseinterface.IDeleteProduct
import mx.itesm.testbasicapi.model.repository.responseinterface.IGetProduct
import mx.itesm.testbasicapi.model.repository.responseinterface.IUpdateProduct
import java.io.ByteArrayOutputStream

class UpdateProductFragment : Fragment() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100;
    }

    private lateinit var btnTakePhoto: Button
    private lateinit var btnRemovePhoto: Button
    private lateinit var btnUpdateProduct: Button
    private lateinit var btnDeleteProduct: Button
    private lateinit var imgProductPhoto: ImageView

    private lateinit var txtProductName: EditText
    private lateinit var txtProductPrice: EditText

    private lateinit var imageByteArray: ByteArray

    private lateinit var productRef: Product
    private lateinit var model: Model

    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getString(PRODUCT_ID_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = Model(Utils.getToken(requireContext()))
        imageByteArray = ByteArray(0)
        btnTakePhoto = view.findViewById(R.id.btnTakePhotoUpdateProduct)
        btnRemovePhoto = view.findViewById(R.id.btnRemovePhoto)
        btnUpdateProduct = view.findViewById(R.id.btnUpdateProduct)
        btnDeleteProduct = view.findViewById(R.id.btnDeleteProduct)
        imgProductPhoto = view.findViewById(R.id.imgProductPhotoUpdateProduct)
        txtProductName = view.findViewById(R.id.txtProductNameUpdateProduct)
        txtProductPrice = view.findViewById(R.id.txtProductPriceUpdateProduct)

        btnTakePhoto.setOnClickListener(clickListenerForTakePhoto())
        btnRemovePhoto.setOnClickListener(clickListenerForRemovePhoto())
        btnUpdateProduct.setOnClickListener(clickListenerForUpdateProduct())
        btnDeleteProduct.setOnClickListener(clickListenerForDeleteProduct())

        prepareGUI()
    }

    private fun clickListenerForUpdateProduct(): View.OnClickListener? {
        return View.OnClickListener {
            productRef.name = txtProductName.text.toString()
            productRef.price = txtProductPrice.text.toString().toDouble()

            model.updateProduct(productRef, imageByteArray, object : IUpdateProduct {
                override fun onSuccess(product: Product?) {
                    Toast.makeText(requireContext(), "Product updated ok", Toast.LENGTH_SHORT)
                        .show()
                    productRef = product!!
                    parentFragmentManager.popBackStackImmediate();
                }

                override fun onNoSuccess(code: Int, message: String) {
                    Toast.makeText(
                        requireContext(),
                        "Problem detected $code $message",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("updateProduct", "$code: $message")
                }

                override fun onFailure(t: Throwable) {
                    Toast.makeText(
                        context,
                        "Network or server error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("updateProduct", t.message.toString())
                }
            } )
        }
    }

    private fun clickListenerForDeleteProduct(): View.OnClickListener? {
        return View.OnClickListener {
            model.deleteProduct(productRef.id, object : IDeleteProduct {
                override fun onSuccess(product: Product?) {
                    Toast.makeText(requireContext(), "Product deleted ok", Toast.LENGTH_SHORT)
                        .show()
                    productRef = product!!
                    parentFragmentManager.popBackStackImmediate();
                }

                override fun onNoSuccess(code: Int, message: String) {
                    Toast.makeText(
                        requireContext(),
                        "Problem detected $code $message",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("deleteProduct", "$code: $message")
                }

                override fun onFailure(t: Throwable) {
                    Toast.makeText(
                        context,
                        "Network or server error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("deleteProduct", t.message.toString())
                }
            })
        }
    }


    private fun prepareGUI() {
        if (productId != null) {
            model.getProduct(productId!!, object : IGetProduct {
                override fun onSuccess(product: Product?) {
                    if (product != null) {
                        productRef = product
                        txtProductName.setText(productRef.name)
                        txtProductPrice.setText(productRef.price.toString())

                        getPhoto()
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

    private fun clickListenerForRemovePhoto(): View.OnClickListener? {
        return View.OnClickListener {
            imageByteArray = ByteArray(0)
            imgProductPhoto.setImageResource(R.drawable.ic_empty);
            productRef.photoPath = null
            // keepPhoto = false
        }
    }

    private fun getPhoto() {
        val photoPath = productRef.photoPath
        if (photoPath != null && photoPath.isNotBlank()) {
            val picasso = RemoteRepository.getPicassoInstance(
                requireContext(),
                Utils.getToken(requireContext())
            )
            val urlForImage = "${Utils.BASE_URL}products/images/$photoPath"
            picasso.load(urlForImage).into(imgProductPhoto);
            // val urlForImage = "${Utils.BASE_URL}products/images/${productRef.photoPath}"
            // Picasso.get().load(urlForImage).into(imgProductPhoto)
        }
    }

    private fun clickListenerForTakePhoto(): View.OnClickListener {
        return View.OnClickListener {
            val permissionStatus = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            )
            if (permissionStatus == PackageManager.PERMISSION_DENIED) {
                Log.i("tag", "we dont have the permission, wil ask for it")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    UpdateProductFragment.CAMERA_PERMISSION_CODE
                )
            } else {
                Log.i("tag", "we have the permission, thanks")
                takePhoto()
            }
        }
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val stream = ByteArrayOutputStream()
                val data = result.data
                val bmp = data?.extras?.get("data") as Bitmap
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                imageByteArray = stream.toByteArray()

                val bitmap = BitmapFactory.decodeByteArray(
                    imageByteArray, 0,
                    imageByteArray.size
                )
                imgProductPhoto.setImageBitmap(bitmap)
            } else {
                Toast.makeText(requireContext(), "Picture was not taken", Toast.LENGTH_SHORT).show()
            }
        }
}