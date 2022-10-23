package com.androidstudioprojects.parseinstagram2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androidstudioprojects.parseinstagram2.Post
import com.androidstudioprojects.parseinstagram2.PostAdapter
import com.androidstudioprojects.parseinstagram2.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery


open class FeedFragment : Fragment() {

    lateinit var swipeContainer: SwipeRefreshLayout

    lateinit var postsRecyclerView: RecyclerView

    lateinit var adapter : PostAdapter

    var allPosts : MutableList<Post>  = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeContainer = view.findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing timeline")
            queryPosts()
            swipeContainer.setRefreshing(false)
        }
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        postsRecyclerView = view.findViewById(R.id.postRecyclerView)
        adapter = PostAdapter(requireContext(), allPosts)
        postsRecyclerView.adapter = adapter
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        queryPosts()
    }

    fun clear() {
        allPosts.clear()
        adapter.notifyDataSetChanged()
    }

    fun addAll(posts: List<Post>) {
        allPosts.addAll(posts)
        adapter.notifyDataSetChanged()
    }

    open fun queryPosts(){
        clear()
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.addDescendingOrder("createdAt")
        query.setLimit(20);
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null){
                    Log.e(TAG, "Error fetching posts")
                }
                else{
                    if (posts != null){
                        for (post in posts){
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: " + post.getUser()?.username)
                        }
                        addAll(posts)
                    }
                }
            }

        })
    }

    companion object{
        const val TAG = "FeedFragment"
    }
}