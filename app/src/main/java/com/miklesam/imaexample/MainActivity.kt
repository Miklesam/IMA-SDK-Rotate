package com.miklesam.imaexample

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.AdsConfiguration
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity() {

    var adsLoader: ImaAdsLoader? = null
    private val viewModel by viewModels<ImaViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MultiDex.install(this)
        if (savedInstanceState == null) {
            viewModel.playerView = findViewById(R.id.player_view)
            //Create an AdsLoader.
            adsLoader = ImaAdsLoader.Builder(this)
                .build()
            initializePlayer()
        }
        // Way to save ad information when rotate
        //(viewModel.playerView?.parent as ViewGroup).removeView(viewModel.playerView)
        //findViewById<FrameLayout>(R.id.container).addView(viewModel.playerView)
        //
        attachPlayer()
    }

    private fun initializePlayer() {
        // Set up the factory for media sources, passing the ads loader and ad view providers.
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(
                this,
                Util.getUserAgent(
                    this,
                    getString(R.string.app_name)
                )
            )

        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
            .setAdsLoaderProvider { unusedAdTagUri: AdsConfiguration? -> adsLoader }
            .setAdViewProvider(viewModel.playerView)
        // Create a SimpleExoPlayer and set it as the player for content and ads.
        viewModel.player =
            SimpleExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build()
        viewModel.playerView?.player = viewModel.player
        adsLoader?.setPlayer(viewModel.player)
        // Create the MediaItem to play, specifying the content URI and ad tag URI.
        val contentUri = Uri.parse(getString(R.string.content_url))
        val adTagUri = Uri.parse(getString(R.string.ad_tag_url_skip))
        val mediaItem =
            MediaItem.Builder().setUri(contentUri)
                .setAdTagUri(adTagUri).build()
        // Prepare the content and ad to be played with the SimpleExoPlayer.
        viewModel.player?.setMediaItem(mediaItem)
        viewModel.player?.prepare()
        // Set PlayWhenReady. If true, content and ads will autoplay.
        viewModel.player?.playWhenReady = true

    }

    private fun attachPlayer() {
        val playerV = findViewById<PlayerView>(R.id.player_view)
        playerV?.player = viewModel.player
    }
}