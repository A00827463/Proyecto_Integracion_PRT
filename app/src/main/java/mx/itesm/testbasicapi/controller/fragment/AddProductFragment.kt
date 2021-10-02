package mx.itesm.testbasicapi.controller.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
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
import mx.itesm.testbasicapi.R
import mx.itesm.testbasicapi.Utils
import mx.itesm.testbasicapi.model.Model
import mx.itesm.testbasicapi.model.entities.Product
import mx.itesm.testbasicapi.model.repository.responseinterface.IAddProduct
import java.io.ByteArrayOutputStream

class AddProductFragment : Fragment() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private lateinit var btnTakePhoto: Button
    private lateinit var btnAddProduct: Button
    private lateinit var productPhoto: ImageView

    private lateinit var model: Model
    private lateinit var imageByteArray: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = Model(Utils.getToken(requireContext()))
        imageByteArray = ByteArray(0)
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto)
        btnAddProduct = view.findViewById(R.id.btnAddProduct)
        productPhoto = view.findViewById(R.id.imgProductPhotoAddProduct)

        btnTakePhoto.setOnClickListener (clickListenerForTakePhoto())
        btnAddProduct.setOnClickListener(clickListenerForAddProduct())
    }

    private fun clickListenerForAddProduct(): View.OnClickListener? {
        return View.OnClickListener {
            val txtProductName = requireView().findViewById<EditText>(R.id.txtProductNameAddProduct)
            val txtProductPrice =
                requireView().findViewById<EditText>(R.id.txtProductPriceAddProduct)

            val product = Product(
                "",
                txtProductName.text.toString(),
                txtProductPrice.text.toString().toDouble(),
                null
            )
            model.addProduct(product, imageByteArray, object : IAddProduct {
                override fun onSuccess(product: Product?) {
                    Toast.makeText(requireContext(), "Product added ok", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStackImmediate();
                }

                override fun onNoSuccess(code: Int, message: String) {
                    Toast.makeText(requireContext(), "Problem detected $code $message", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(t: Throwable) {
                    Toast.makeText(requireContext(), "Network or server error occurred", Toast.LENGTH_SHORT).show()
                }
            })
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
                    CAMERA_PERMISSION_CODE
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
                productPhoto.setImageBitmap(bitmap)
            } else {
                Toast.makeText(requireContext(), "Picture was not taken", Toast.LENGTH_SHORT).show()
            }
        }
}