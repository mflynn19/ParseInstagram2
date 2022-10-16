package com.androidstudioprojects.parseinstagram2

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.androidstudioprojects.parseinstagram2.LoginActivity.Companion.TAG
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ParcelableSparseIntArray
import com.parse.*
import java.io.File

class MainActivity : AppCompatActivity() {
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //queryPosts()

        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            val description = findViewById<EditText>(R.id.etDescription).text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null){
                submitPost(description, user, photoFile!!)
            }
            else{
                Toast.makeText(this, "Take a picture!", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.btnCamera).setOnClickListener {
            onLaunchCamera()
        }

        findViewById<FloatingActionButton>(R.id.fabLogout).setOnClickListener{
            ParseUser.logOut()
            goToLoginActivity()
            finish()
        }
    }

    private fun goToLoginActivity(){
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun submitPost(description: String, user: ParseUser, file: File) {
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground { exception ->
            if (exception != null){
                Log.e(TAG, "error while saving post")
                exception.printStackTrace()
                Toast.makeText(this, "error while saving", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.i(TAG, "successfully posted")
                Toast.makeText(this, "Great post!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                val ivPreview = findViewById<ImageView>(R.id.ivImage)
                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(this, "Error taking picture", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFileUri(photoFileName)

        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun queryPosts(){
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post>{
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null){
                    Log.e(TAG, "Error fetching posts")
                }
                else{
                    if (posts != null){
                        for (post in posts){
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: " + post.getUser()?.username)
                        }
                    }
                }
            }

        })
    }
    companion object{
        val TAG = "MainActivity"
    }
}