package com.androidstudioprojects.parseinstagram2.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView
import androidx.core.content.FileProvider
import com.androidstudioprojects.parseinstagram2.LoginActivity
import com.androidstudioprojects.parseinstagram2.MainActivity
import com.androidstudioprojects.parseinstagram2.Post
import com.androidstudioprojects.parseinstagram2.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File

class ComposeFragment : Fragment() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null
    lateinit var ivPreview: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ivPreview = view.findViewById<ImageView>(R.id.ivImage)

        view.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            val description = view.findViewById<EditText>(R.id.etDescription).text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null){
                submitPost(description, user, photoFile!!)
            }
            else{
                Toast.makeText(context, "Take a picture!", Toast.LENGTH_SHORT).show()
            }
        }
        view.findViewById<Button>(R.id.btnCamera).setOnClickListener {
            onLaunchCamera()
        }

        view.findViewById<FloatingActionButton>(R.id.fabLogout).setOnClickListener{
            ParseUser.logOut()
            //goToLoginActivity()
            //finish()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun submitPost(description: String, user: ParseUser, file: File) {
        val pb = view?.findViewById<ProgressBar>(R.id.pbLoading);
        pb?.setVisibility(ProgressBar.VISIBLE);
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground { exception ->
            if (exception != null){
                Log.e(MainActivity.TAG, "error while saving post")
                exception.printStackTrace()
                Toast.makeText(requireContext(), "error while saving", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.i(MainActivity.TAG, "successfully posted")
                Toast.makeText(requireContext(), "Great post!", Toast.LENGTH_SHORT).show()
                pb?.setVisibility(ProgressBar.INVISIBLE);
            }
        }
    }

    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFileUri(photoFileName)

        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), MainActivity.TAG)

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(MainActivity.TAG, "failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

    /*private fun goToLoginActivity(){
        val intent = Intent(this@ComposeFragment, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)

                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(requireContext(), "Error taking picture", Toast.LENGTH_SHORT).show()
            }
        }
    }

}