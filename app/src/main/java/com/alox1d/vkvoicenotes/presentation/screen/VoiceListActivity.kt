package com.alox1d.vkvoicenotes.presentation.screen

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import com.alox1d.vkvoicenotes.App
import com.alox1d.vkvoicenotes.R
import com.alox1d.vkvoicenotes.data.model.VoiceNote
import com.alox1d.vkvoicenotes.databinding.ActivityMainBinding
import com.alox1d.vkvoicenotes.databinding.DialogSetFileNameBinding
import com.alox1d.vkvoicenotes.internal.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_CODE
import com.alox1d.vkvoicenotes.presentation.adapter.OnVoiceListAdapterListener
import com.alox1d.vkvoicenotes.presentation.adapter.VoiceNotesAdapter
import com.alox1d.vkvoicenotes.presentation.viewmodel.VoiceListViewModel
import com.android.player.BaseSongPlayerActivity
import com.android.player.model.AVoiceNote
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class VoiceListActivity : BaseSongPlayerActivity(), OnVoiceListAdapterListener {

    lateinit var mAdapterVoice: VoiceNotesAdapter
    private lateinit var binding: ActivityMainBinding
    private val viewModel: VoiceListViewModel by viewModels()

    private lateinit var fullVoiceFilePath: String;
    private lateinit var voiceFilePath: String;
    private lateinit var voiceFile: File
    private lateinit var voicesDirectoryPath: String
    private lateinit var voicesDirectory: File
    private var date: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        App.daggerAppComponent.inject(viewModel)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setUpRecyclerView()
        observeLiveData()
        setUpTitle()
        setUpPermissions()
        setUpListeners()
        setUpNotesDirectory()

    }

    private fun setUpNotesDirectory() {
        voicesDirectoryPath =
            getExternalFilesDir(null)?.absolutePath + File.separator + "VoiceNotes" + File.separator
        voicesDirectory = File(voicesDirectoryPath)
        if (!voicesDirectory.exists()) {
            voicesDirectory.mkdirs()
        }
    }

    private fun setUpListeners() {
        binding.fab.setOnClickListener {
            viewModel.recording.postValue(!(viewModel.recording.value)!!)
        }
    }

    private fun setUpTitle() {
        supportActionBar?.title =
            getString(R.string.notes) // под каждую ли переменную заводить LiveData?
    }

    private fun setUpPermissions() {
        if (!isReadPhoneStatePermissionGranted())
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                ),
                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_CODE
            )
    }

    protected var recorder: MediaRecorder? = null

    private fun startRecording(fileName: String) {
        // initialize and configure MediaRecorder
        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFile(fileName)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        try {
            recorder!!.prepare()
            recorder!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
            // handle error
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            // handle error
        }
    }
    private fun stopRecording(): Boolean {
        // stop recording and free up resources
        recorder?.let {

            recorder!!.stop()
            recorder!!.release()
            recorder = null
            val bindAlert  = DialogSetFileNameBinding.inflate(layoutInflater)
            val userInput = bindAlert.editTextDialogUserInput
            val cancel: Button = bindAlert.saveCancel
            val ok: Button = bindAlert.saveOk

            val builder =
            MaterialAlertDialogBuilder(this,
                R.style.NotesThemeOverlay_MaterialComponents_MaterialAlertDialog)
            // set prompts.xml to alertdialog builder
            builder.setView(bindAlert.root)
            builder.setCancelable(false)
            val dialog = builder.create()
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            ok.setOnClickListener {
                val newFileName = userInput.text.toString()
                if (newFileName.trim { it <= ' ' }.length > 0) {
                    voiceFilePath = "$newFileName.aac"
                    fullVoiceFilePath = voicesDirectoryPath + voiceFilePath
                    val newFile = File(voicesDirectoryPath, voiceFilePath)
                    voiceFile.renameTo(newFile)
                    viewModel.isNameSet.value = true
                    dialog.dismiss()
                } else {
                    viewModel.isNameSet.value = true
                    dialog.dismiss()
                }
            }
            dialog.show()
            // create and show the alert dialog

            return true
        }
        return false

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_CODE -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {// Permission Granted
//                    openMusicList()
                } else {
                    // Permission Denied
                    Snackbar.make(
                        binding.recyclerView,
                        getString(R.string.you_denied_permission),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getVoiceNotesFromDB()
    }

    override fun onStop() {
        super.onStop()
        if (stopRecording()){
//                    val uri: Uri = Uri.parse(file.absolutePath)
//                    val mmr = MediaMetadataRetriever()
//                    mmr.setDataSource(application, uri)
//                    val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//                    val millSecond = durationStr!!
    val note = VoiceNote(
                name = voiceFilePath,
                path = fullVoiceFilePath,
                duration = "",
                date = date
            )
            viewModel.saveSongData(note)
        }

    }
    private fun setUpRecyclerView() {
        mAdapterVoice = VoiceNotesAdapter(this)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            this.adapter = mAdapterVoice
        }
    }
    private fun observeLiveData() {
        viewModel.playlistData.observe(this) {
            mAdapterVoice.notes = it
        }
        audioPlayerViewModel.isPlayData.observe(this) {
            mAdapterVoice.playingViewHolder?.itemBinding?.playButton?.setImageResource(if (it) R.drawable.ic_pause_vector else R.drawable.ic_play_vector)
        }
        viewModel.recording.observe(this) {
            if (it) {
                if (isRecordPermissionGranted()) {
                    date = Calendar.getInstance().timeInMillis
                    voiceFilePath = "Запись " + SimpleDateFormat("dd.MM.yy HH:mm:ss").format(date) +".aac"
                    fullVoiceFilePath = voicesDirectoryPath+ voiceFilePath
                    voiceFile = File(voicesDirectory, voiceFilePath)
                    val snack = Snackbar.make(binding.root,"НАЧАТА ЗАПИСЬ",Snackbar.LENGTH_LONG)
                    snack.setAction("ОК", View.OnClickListener {
                        snack.dismiss()
                    })
                    snack.show()
                    binding.fab.animate().setDuration(200).rotation(180f)
                    startRecording(fullVoiceFilePath)


                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_CODE + 1
                    );
                }
            } else {
                if (recorder!=null){
                    binding.fab.animate().setDuration(200).rotation(0f)
                    stopRecording()
//                    val uri: Uri = Uri.parse(file.absolutePath)
//                    val mmr = MediaMetadataRetriever()
//                    mmr.setDataSource(application, uri)
//                    val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//                    val millSecond = durationStr!!


                }

            }

        }
        viewModel.isNameSet.observe(this){
            if (it){
                val note = VoiceNote(
                    name = voiceFilePath,
                    path = fullVoiceFilePath,
                    duration = "",
                    date = date
                )
                viewModel.saveSongData(note)
                viewModel.isNameSet.value = !it
            }
        }

//        observeInProgress()
//        observeIsError()
//        observeAudious()
    }
    override fun playNote(note: VoiceNote, voiceNotes: ArrayList<VoiceNote>) {

        viewModel.onPlayClicked()
        play(viewModel.playlistData.value as MutableList<AVoiceNote>, note)
//        AudioPlayerActivity.start(this, note, voiceNotes)
    }
    override fun removeNoteItem(note: VoiceNote) {
        showRemoveNoteItemConfirmDialog(note)
    }

    private fun showRemoveNoteItemConfirmDialog(note: VoiceNote) {
        // setup the alert builder
        MaterialAlertDialogBuilder(this,
            R.style.NotesThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setMessage(getString(R.string.sure_remove))
            // add a button
            .apply {
                setPositiveButton(R.string.yes) { _, _ ->
                    removeAudioFromList(note)
                }
                setNegativeButton(R.string.no) { _, _ ->
                    // User cancelled the dialog
                }
            }
            // create and show the alert dialog
            .show()
    }
    private fun removeAudioFromList(note: VoiceNote) {
        audioPlayerViewModel.stop()
        viewModel.removeItemFromList(note)
    }
    private fun isReadPhoneStatePermissionGranted(): Boolean {
        val firstPermissionResult = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return firstPermissionResult == PackageManager.PERMISSION_GRANTED
    }
    private fun isRecordPermissionGranted(): Boolean {

        val firstPermissionResult = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
        return firstPermissionResult == PackageManager.PERMISSION_GRANTED
    }
//    private fun observeInProgress() {
//        viewModel.repository.isInProgress.observe(this) { isLoading ->
//            isLoading.let {
//                if (it) {
//                    binding.emptyText.visibility = View.GONE
//                    binding.recyclerView.visibility = View.GONE
//                    binding.fetchProgress.visibility = View.VISIBLE
//                } else {
//                    binding.fetchProgress.visibility = View.GONE
//                }
//            }
//        }
//    }


//    private fun observeIsError() {
//        viewModel.repository.isError.observe(this) { isError ->
//            isError.let {
//                if (it) {
//                    disableViewsOnError()
//                } else {
//                    binding.emptyText.visibility = View.GONE
//                    binding.fetchProgress.visibility = View.VISIBLE
//                }
//            }
//        }
//    }
//    private fun observeAudious() {
//        viewModel.repository.data.observe(this) { giphies ->
//            giphies.let {
//                if (it != null && it.isNotEmpty()) {
//                    binding.fetchProgress.visibility = View.VISIBLE
//                    binding.recyclerView.visibility = View.VISIBLE
//                    mAdapter.setUpData(it)
//                    binding.emptyText.visibility = View.GONE
//                    binding.fetchProgress.visibility = View.GONE
//                } else {
//                    disableViewsOnError()
//                }
//            }
//        }
//    }
//    private fun disableViewsOnError() {
//        binding.fetchProgress.visibility = View.VISIBLE
//        binding.emptyText.visibility = View.VISIBLE
//        binding.recyclerView.visibility = View.GONE
//        mAdapter.setUpData(emptyList())
//        binding.fetchProgress.visibility = View.GONE
//    }

}