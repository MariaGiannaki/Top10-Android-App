package com.example.top10

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates




class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""

    override fun toString(): String {
        return """
            name = $name
            artist = $artist
            releaseDate = $releaseDate
            imageURL = $imageURL
        """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {

    private var loadData: LoadData? = null
    private var feedUrl: String = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/%s/limit=%d/xml"
    private var feedType: String = "topfreeapplications"
    private var feedLimit: Int = 10
    private var cashedFeedUrl: String = "Url to be set"

    private val tabLayout by lazy(LazyThreadSafetyMode.NONE) {findViewById<TabLayout>(R.id.tabLayout)}

    private val STATE_FEEDLIMIT = "limit"
    private val STATE_FEEDTYPE = "topfreeapplications"
    private val STATE_TAB = "tabposition"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("LogTest", "On cerate called")
        //val loadData = LoadData(this, xmlListView)
        DownloadUrl(feedUrl.format(feedType, feedLimit))

       // val tabLayout: TabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                //Log.d("tab", "reselected tab ${tab?.position}")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //Log.d("tab", "unselected tab ${tab?.position}")
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d("tab", "clicked tab ${tab?.position}")
                when(tab?.position) {
                    0 -> feedLimit = 10
                    1 -> feedLimit = 25
                    2 -> feedLimit = 50
                }
                DownloadUrl(feedUrl.format(feedType, feedLimit))
            }

        })

        // restore state
        if (savedInstanceState != null) {
            feedLimit = savedInstanceState.getInt(STATE_FEEDLIMIT)
            feedType = savedInstanceState.getString(STATE_FEEDTYPE)!!
            tabLayout.selectTab(tabLayout.getTabAt(savedInstanceState.getInt(STATE_TAB)), true)
            DownloadUrl(feedUrl.format(feedType, feedLimit))
        }

    }

    private fun DownloadUrl(feedUrl: String) {
        // only download data when the url has changed
        if (feedUrl != cashedFeedUrl) {
            loadData = LoadData(this, xmlListView)
            loadData?.execute(feedUrl)
            cashedFeedUrl = feedUrl
            Log.d("LogTest", "on create done")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)
        return true
    }

    // topbar menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val feedUrl: String = this.feedUrl

        when (item.itemId) {
            R.id.menuFree ->
                feedType = "topfreeapplications"
            R.id.menuPaid ->
                feedType = "toppaidapplications"
            R.id.menuSongs ->
                feedType = "topsongs"
            R.id.menuMovies ->
                feedType = "topmovies"
            R.id.refreshButton ->
                cashedFeedUrl = "Url to be set"
            else ->
                return super.onOptionsItemSelected(item)
        }

        DownloadUrl(feedUrl.format(feedType, feedLimit))
        return true
    }

    // save state url on orientation change
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_FEEDLIMIT, feedLimit)
        outState.putString(STATE_FEEDTYPE, feedType)
        outState.putInt(STATE_TAB, tabLayout.selectedTabPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        loadData?.cancel(true)
    }

    companion object {
        private class LoadData(context: Context ,listView: ListView): AsyncTask<String, Void, String>() {
            // use delegates to avoid memory leaks?
            // can't use this because inner class so pass a context to have access in adapter
            var context: Context by Delegates.notNull()
            var listView: ListView by Delegates.notNull()

            init {
                this.context = context
                this.listView = listView
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                val parseApplications = ParseXMLData()
                parseApplications.Parse(result)

                val feedAdapter = FeedAdapter(context, R.layout.list_record, parseApplications.applications)
                listView.adapter = feedAdapter
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d("LogTest", "Doing background starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e("LogTest", "doInBackground: Error downloading data")
                }
                return rssFeed
            }
        }

        private fun downloadXML(urlPath: String?): String {
            return URL(urlPath).readText()
//            val xmlResult = StringBuilder()
//
//            try {
//                val url = URL(urlPath)
//                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//                val response = connection.responseCode
//                Log.d("LogTest", "downloadXML: Response code $response")
//
//
//                connection.inputStream.buffered().reader().use {
//                    xmlResult.append(it.readText())
//                }
//
//                Log.d("LogTest", "Received ${xmlResult.length} bytes")
//                return xmlResult.toString()
//            } catch (e: Exception) {
//                val errorMessage: String = when (e) {
//                    is MalformedURLException -> "downloadXML: Invalid URL ${e.message}"
//                    is IOException -> "downloadXML: IO Exception reading data ${e.message}"
//                    is SecurityException -> "downloadXML: Security / access exception ${e.message}"
//                    else -> "downloadXML: Unknown error ${e.message}"
//                }
//            }
//            // if we get here there is a problem so return empty string
//            return ""
        }
    }



}