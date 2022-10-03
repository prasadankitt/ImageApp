package com.example.cameragallery

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
//Everywhere the commented section is used when we are storing bitmap in database by converting it into bytearray

class MainActivity : AppCompatActivity() {

    val CAMERA_REQUEST_CODE = 230
    val GALLERY_REQUEST_CODE = 199
    private lateinit var rv: RecyclerView
    private lateinit var adapter: Adapter
    private lateinit var clickToUpload: ImageView
    private lateinit var showButton: Button
    private lateinit var viewModel: OurViewmodel
    private var picture: Bitmap? = null
    private var imageUri:Uri? = null
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)

        rv = findViewById(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = Adapter(this)
        rv.adapter  = adapter

        viewModel = OurViewmodel(application)
        viewModel.allData.observe(this, Observer{ it ->
            it?.let{adapter.updateList(it) }
        })

        clickToUpload = findViewById(R.id.imageView)
        clickToUpload.setOnClickListener{

            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf("Open Gallery","Open Camera")
            pictureDialog.setItems(pictureDialogItem) {
                    dialog, which ->
                when(which){
                    0->galleryCheckPermission()
                    1->cameraCheckPermission()
                }
            }
            pictureDialog.show()
        }

        showButton = findViewById(R.id.showButton)
        showButton.setOnClickListener {
//            if(picture!=null)
//            {
//                val obj = DatabaseData(0,picture)
//                viewModel.insertImage(obj)
//                picture = null
//            }
            if(imageUri!=null)
            {
                val obj = DatabaseData(0,imageUri.toString())
                viewModel.insertImage(obj)
                imageUri = null
            }
            else
            {
                Toast.makeText(this,"Select Image",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cameraCheckPermission()
    {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(
                object : MultiplePermissionsListener
                {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if(report.areAllPermissionsGranted()) {
                                camera()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRotationalDialogForPermission()
                    }
                }
            ).onSameThread().check()
    }
    private fun galleryCheckPermission()
    {
        Dexter.withContext(this).withPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object: PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(this@MainActivity,"Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery()
    {
        intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun camera(){
        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
            when(requestCode)
            {
//                CAMERA_REQUEST_CODE->{
//                    picture = data?.extras?.get("data") as Bitmap
//                }
                CAMERA_REQUEST_CODE->{
                    picture = data?.extras?.get("data") as Bitmap
                    imageUri = getImageUriFromBitmap(applicationContext,picture!!)
                }

//                GALLERY_REQUEST_CODE->{
//                    data!!.data.also{ imageUri = it }
//                    picture = MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
//                }
                GALLERY_REQUEST_CODE->{
                    data!!.data.also{ imageUri = it }
                    picture = MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
                    imageUri = getImageUriFromBitmap(applicationContext,picture!!)
                }
            }
        }
    }

    private fun showRotationalDialogForPermission(){
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions"
                    +"required for this feature. It can be enable from App Settings")
            .setPositiveButton("Go to settings"
            ) { _, _ ->
                try
                {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }
                catch (e: ActivityNotFoundException)
                {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"
            ) { dialog,_ ->
                dialog.dismiss()
            }.show()
    }

    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val path  = MediaStore.Images.Media.insertImage(context.contentResolver,bitmap,"File",null)
        return  Uri.parse(path.toString())
    }

}