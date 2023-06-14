package com.example.intro_companion.frontend.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.intro_companion.GeoFences
import com.example.intro_companion.backend.entities.Article
import com.example.intro_companion.databinding.FragmentSuggestionsBinding
import com.example.intro_companion.frontend.adapters.ArticleAdapter
import com.example.intro_companion.frontend.models.RequestViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SuggestionsFragment : Fragment() {

    /* Login screen binding */
    private lateinit var _binding: FragmentSuggestionsBinding

    private lateinit var _viewModel: RequestViewModel

    private var articles: MutableList<Article> = mutableListOf()

    /* ViewModel controller */
//    private val requestViewModel: RequestViewModel by viewModels()

    /**
     * Override method onCreateView to create and return the fragment's view hierarchy.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /* Inflate the layout */
        _binding =
            FragmentSuggestionsBinding.inflate(inflater, container, false)

        _binding.suggestedArticles.layoutManager = LinearLayoutManager(context)

        _viewModel = ViewModelProvider(requireActivity())[RequestViewModel::class.java]

        requestArticlesHttp(_viewModel.request)

        return _binding.root
    }

    /**
     * Called when the view for this fragment has been created. Sets click listeners for two buttons
     * related to authentication and navigation to the Home destination.
     *
     * @param view The view for this fragment.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.description.setText(_viewModel.request.value?.description)

        /* Set the click listener for successful resolution */
        _binding.yes.setOnClickListener {
            findNavController().navigate(
                SuggestionsFragmentDirections.suggestionToReturn()
            )
        }

        /* Set the click listener for successful resolution */
        _binding.submitToIts.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                _viewModel.addRequest()
            }

            findNavController().navigate(
                SuggestionsFragmentDirections.suggestionToConfirm()
            )
        }

        /* Set the click listener for unsuccessful resolution */
        _binding.no.setOnClickListener {
            _binding.submitContainer.visibility = View.VISIBLE
        }

    }

    /**
     * Sends a HTTP POST request to the backend server to request articles related to a given request.
     *
     * @param body A LiveData object representing the request to be sent to the server.
     */
    private fun requestArticlesHttp(body: LiveData<com.example.intro_companion.backend.entities.Request>) {

        //Creates coroutine/async task
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        //handles data back in the main thread for UI updates
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            try {
                //JSON parser
                val gson = Gson()
                //Creates connection to Endpoint
                val url = URL(ENDPOINT)

                val connection = url.openConnection() as HttpURLConnection
                var responseBody = ""

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.doOutput = true

                val writer = BufferedWriter(OutputStreamWriter(connection.outputStream))

                writer.write(gson.toJson(body))
                writer.flush()
                writer.close()

                //open stream to process server's response
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                reader.forEachLine { responseBody += it }

                reader.close()
                //close connection
                connection.disconnect()

                var responseJSON = JSONObject(responseBody)

                /* Handle edge case where user goes back */
                articles.clear()

                handler.post {
                    if (responseJSON.get(ARTICLES).toString() == "null") {
                        articles.add(
                            Article(
                                "Cannot find relevant article",
                                ""
                            )
                        )
                    } else {
                        if (GeoFences.campusRegion == "" && responseJSON.getString(NETWORK) == "Must") {
                            articles.add(
                                Article(
                                    responseJSON.getJSONObject(VPN_LINK).getString(TITLE)
                                        .toString(),
                                    responseJSON.getJSONObject(VPN_LINK).getString(LINK)
                                        .toString()
                                )
                            )
                        }

                        for (i in 0 until responseJSON.getJSONArray(ARTICLES).length()) {
                            articles.add(
                                Article(
                                    responseJSON.getJSONArray(ARTICLES).getJSONObject(i)
                                        .getString(TITLE),
                                    responseJSON.getJSONArray(ARTICLES).getJSONObject(i)
                                        .getString(LINK)
                                )
                            )
                        }

                        _viewModel.setClassification(responseJSON.getString("classification"))
                    }

                    _binding.suggestedArticles.visibility = View.VISIBLE
                    _binding.triedLabel.visibility = View.VISIBLE
                    _binding.buttonContainer.visibility = View.VISIBLE
                    _binding.progressBar.visibility = View.GONE

                    //update the adapter with the articles
                    _binding.suggestedArticles.adapter =
                        ArticleAdapter(articles) { url ->
                            if (url != "")
                                redirectToSite(url)
                        }
                }
            } catch (e: Exception) {
                //log errors as necessary
                e.message?.let { Log.e("Adding Articles:", it) }
            }
        }
    }

    /**
     * Opens a web page by redirecting to the provided URL.
     *
     * The function takes a URL as a parameter and creates an intent with the ACTION_VIEW action to open
     * the URL. It sets the data URI of the intent to the provided URL using Uri.parse(). Finally, it starts
     * the activity with the created intent to open the web page.
     *
     * @param url The URL of the web page to be opened.
     */
    private fun redirectToSite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    companion object {
        private const val ENDPOINT = "http://introcompanion.wpi.edu:8000/"
        private const val NETWORK = "wpi_network"
        private const val VPN_LINK = "vpn_link"
        private const val TITLE = "title"
        private const val LINK = "link"
        private const val ARTICLES = "articles"
    }
}

