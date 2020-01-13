package com.example.qrtest

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
    QRCodeReaderView.OnQRCodeReadListener {

    private val MY_PERMISSION_REQUEST_CAMERA = 0

    private lateinit var mainLayout: ViewGroup

    private var resultTextView: TextView? = null
    private var qrCodeReaderView: QRCodeReaderView? = null
    private var flashlightCheckBox: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main_layout)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initQRCodeReaderView()
        } else {
            requestCameraPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        if (qrCodeReaderView != null) {
            qrCodeReaderView!!.startCamera()
        }
    }

    override fun onPause() {
        super.onPause()

        if (qrCodeReaderView != null) {
            qrCodeReaderView!!.stopCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return
        }

        if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT)
                .show()
            initQRCodeReaderView()
        } else {
            Snackbar.make(
                mainLayout,
                "Camera permission request was denied.",
                Snackbar.LENGTH_SHORT
            )
                .show()
        }
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    override fun onQRCodeRead(text: String, points: Array<PointF>) {
        resultTextView!!.text = text
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(
                mainLayout, "Camera access is required to display the camera preview.",
                Snackbar.LENGTH_INDEFINITE
            ).setAction(
                "OK"
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    MY_PERMISSION_REQUEST_CAMERA
                )
            }.show()
        } else {
            Snackbar.make(
                mainLayout, "Permission is not available. Requesting camera permission.",
                Snackbar.LENGTH_SHORT
            ).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSION_REQUEST_CAMERA
            )
        }
    }

    private fun initQRCodeReaderView() {
        val content = layoutInflater.inflate(R.layout.content_decoder, mainLayout, true)

        qrCodeReaderView = content.findViewById(R.id.qrdecoderview)
        resultTextView = content.findViewById(R.id.result_text_view)
        flashlightCheckBox = content.findViewById(R.id.flashlight_checkbox)

        qrCodeReaderView!!.setAutofocusInterval(2000L)
        qrCodeReaderView!!.setOnQRCodeReadListener(this)
        qrCodeReaderView!!.setBackCamera()
        flashlightCheckBox!!.setOnCheckedChangeListener { _, isChecked ->
            qrCodeReaderView!!.setTorchEnabled(isChecked)
        }
        qrCodeReaderView!!.setQRDecodingEnabled(true)
        qrCodeReaderView!!.startCamera()
    }
}
