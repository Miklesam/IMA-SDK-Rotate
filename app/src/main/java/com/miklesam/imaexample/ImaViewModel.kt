package com.miklesam.imaexample

import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class ImaViewModel : ViewModel() {
    var player: SimpleExoPlayer? = null
    var playerView: PlayerView? = null
}