package com.vladgroshkov.automatedlibrary

import AlertCustomDialog
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.SparseArray
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception

private const val USER_ID = "userId"

class TakeFragment : Fragment() {

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector

    private lateinit var cameraSurfaceView: SurfaceView
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_give, container, false)
        cameraSurfaceView = view.findViewById(R.id.cameraSurfaceView)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askForCameraPermission()
        } else {
            setupControls()
        }

        return view
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            requestCodeCameraPermission)
    }

    private fun setupControls() {
        detector = BarcodeDetector.Builder(requireActivity()).build()
        cameraSource = CameraSource.Builder(requireActivity(), detector)
            .setAutoFocusEnabled(true)
            .build()
        cameraSurfaceView.holder.addCallback(surfaceCallback)
        detector.setProcessor(processor)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                AlertCustomDialog(requireContext()).showErrorDialog("Не удалось получить разрешения на использование камеры")
            }
        }
    }

    private val surfaceCallback = object: SurfaceHolder.Callback {
        @SuppressLint("MissingPermission")
        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                cameraSource.start(holder)
            } catch (e: Exception) {
                AlertCustomDialog(requireContext()).showErrorDialog("Что-то пошло не так. Попробуйте еще раз")
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            cameraSource.stop()
        }

    }

    private val processor = object: Detector.Processor<Barcode> {
        override fun release() {

        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            if (cameraSurfaceView.holder != null && detections.detectedItems.isNotEmpty()) {
                val qrCodes: SparseArray<Barcode> = detections.detectedItems
                val code = qrCodes.valueAt(0)
                if (code.displayValue != "null")
                    onSuccessDetecting(code.displayValue)

            }
        }

    }

    private fun onSuccessDetecting(value: String) {
        val globalBookId = getGlobalBookId(value)
        val myRef = Firebase.database.getReference("").child("books").child(globalBookId)
        myRef.get().addOnSuccessListener{ task ->
            val result = task
            var quantity = result.child("quantityBook").value.toString()
            if (quantity != "null") {
                val quantityOfBook = quantity.toInt()
                myRef.child("quantityBook").setValue(quantityOfBook+1)
                addBookToUser(globalBookId, myRef.child("quantityBook"))
            }
        }
    }

    private fun addBookToUser(globalBookId: String, book: DatabaseReference) {
        val myRef = Firebase.database.getReference("").child("users").child(userId!!).child("books")
        myRef.child(globalBookId).removeValue()
        val frag = ReaderInfoFragment.newInstance(userId!!)
        (activity as MainActivity).replaceFragment(frag, ReaderInfoFragment.TAG)
        cameraSource.stop()
    }

    private fun getGlobalBookId(bookId: String): String {
        return bookId.split(" ")[0]
    }

    private fun getPrivateBookId(bookId: String): String {
        return bookId.split(" ")[1]
    }

    companion object {
        var TAG: String = TakeFragment::class.java.simpleName
        @JvmStatic
        fun newInstance(userId: String) =
            TakeFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
                }
            }
    }
}