package com.maxim_tep.googlewebview.ui.view

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.maxim_tep.googlewebview.R

class GoogleFragment: Fragment(R.layout.fragment_google) {
    private var isOnline = false
    private lateinit var noInternetSnackbar: Snackbar
    private lateinit var webView: WebView
    private val mainLooperHandler = Handler(getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val currentView = inflater.inflate(R.layout.fragment_google, container, false)

        webView = currentView.findViewById(R.id.web_view)

        checkNetwork()

        setNetworkChangedListener()

        return currentView
    }

    private fun showGoogleWebView(){
        webView.visibility = VISIBLE
        webView.loadUrl("https://www.google.ru/")
    }

    private fun hideGoogleWebView(){
        webView.visibility = INVISIBLE
    }

    private fun setNetworkChangedListener(){
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if(context == null) return
                isOnline = true
                if(noInternetSnackbar.isShown) noInternetSnackbar.dismiss()
                mainLooperHandler.post {
                    showGoogleWebView()
                }
            }
            override fun onLost(network: Network) {
                isOnline = false
                showNoInternetSnackBar()
                mainLooperHandler.post {
                    hideGoogleWebView()
                }
            }
        }
        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun checkNetwork(){
        if(isOnline) return
        showNoInternetSnackBar()
    }

    private fun showNoInternetSnackBar(){
        noInternetSnackbar = Snackbar
            .make(requireActivity().window.decorView.findViewById(android.R.id.content),
                R.string.no_internet_error, Snackbar.LENGTH_LONG)
            .setAction(R.string.retry_button_text){checkNetwork()}
        noInternetSnackbar.show()
    }
}