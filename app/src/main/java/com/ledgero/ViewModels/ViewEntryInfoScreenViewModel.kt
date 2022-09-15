package com.ledgero.ViewModels

import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.ledgero.DataClasses.Entries
import com.ledgero.R
import com.ledgero.Repositories.ViewEntryInfoScreenRepo
import com.ledgero.fragments.ViewEntryInfoScreen
import java.io.File

class ViewEntryInfoScreenViewModel(private val viewEntryInfoScreenRepo: ViewEntryInfoScreenRepo) : ViewModel() {

    private var dataDownload: LiveData<Int>?=null
    var view: ViewEntryInfoScreen?=null
    var mediaPlayer:MediaPlayer?=null
    var isAudioPlaying:Boolean=false
    lateinit var handler:Handler


    fun getVoiceNote(entry: Entries): LiveData<Int>? {
        dataDownload=getVoiceFileFromFirebase(entry)
        return dataDownload
    }

   private fun getVoiceFileFromFirebase(entry:Entries):LiveData<Int>{

       dataDownload= viewEntryInfoScreenRepo.getVoiceFile(entry)
     return  dataDownload!!

    }

    fun readyVoiceNoteToPlay(entry: Entries):Boolean {

        val fdelete= File(entry.voiceNote!!.localPath)
        if (fdelete.exists()) {
            view!!.voiceLayout.visibility= View.VISIBLE
            return true
        }else{
            view!!.voiceLayout.visibility=View.GONE
           view!!.observeDownload(getVoiceNote(entry)!!)
        }

return false

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun startPlayingAudio() {

        mediaPlayer= MediaPlayer()

        mediaPlayer!!.setOnCompletionListener(){
            view!!.voicePlayButton.foreground= ContextCompat.getDrawable(view!!.requireContext(), R.drawable.ic_play_button)
            isAudioPlaying=false
            stopPlayingAudio() }
        view!!.voicePlayButton.foreground= ContextCompat.getDrawable(view!!.requireContext(), R.drawable.ic_pause)

        mediaPlayer!!.setDataSource(getPathForStoringFile())
        mediaPlayer!!.prepare()


        setUpSeekBar(view!!.seekBar)
        makeHandler(view!!.seekBar)

        isAudioPlaying=true
        mediaPlayer!!.start()

    }

    private fun getPathForStoringFile(): String {

        return view!!.entry.voiceNote!!.localPath.toString()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun stopPlayingAudio(){
        if (mediaPlayer != null) {

            mediaPlayer!!.stop()
          view!!.seekBar.progress=0
            mediaPlayer!!.release()
            mediaPlayer=null
            isAudioPlaying=false
            view!!.voicePlayButton.foreground=ContextCompat.getDrawable(view!!.requireContext(), R.drawable.ic_play_button)
        }

    }
    fun setUpSeekBar(mSeekBar: SeekBar){
        mSeekBar.setMax(mediaPlayer!!.duration)
        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer!!.seekTo(progress)
                }
            }
        })

    }

    private fun makeHandler(mSeekBar: SeekBar) {
        handler= object : Handler(){ }

        var runnable=object : Runnable {
            override fun run() {
                if (mediaPlayer != null) {
                    Log.d("ViewEntryInfoScreen", "run: runnable for seekbar")
                    val mCurrentPosition: Int = mediaPlayer!!.currentPosition
                    mSeekBar.setProgress(mCurrentPosition)
                    handler.postDelayed(this, 100)
                }
            }
        }

        handler.postDelayed(runnable,100)

    }



}