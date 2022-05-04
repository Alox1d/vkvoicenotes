package com.alox1d.vkvoicenotes.presentation.screen

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import com.alox1d.vkvoicenotes.App
import com.alox1d.vkvoicenotes.R
import com.alox1d.vkvoicenotes.databinding.ActivityMainBinding
import com.alox1d.vkvoicenotes.databinding.DialogSetFileNameBinding
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import com.alox1d.vkvoicenotes.internal.REQUEST_PERMISSION_RECORD
import com.alox1d.vkvoicenotes.internal.util.rotateTo180
import com.alox1d.vkvoicenotes.internal.util.rotateToDefault
import com.alox1d.vkvoicenotes.presentation.adapter.OnVoiceListAdapterListener
import com.alox1d.vkvoicenotes.presentation.adapter.VoiceNotesAdapter
import com.alox1d.vkvoicenotes.presentation.viewmodel.VoiceListViewModel
import com.alox1d.vkvoicenotes.recorder.model.RecordingAudio
import com.alox1d.vkvoicenotes.recorder.service.RecordingService
import com.alox1d.vkvoicenotes.recorder.viewmodel.RecorderViewModel
import com.android.player.BaseSongPlayerActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKAuthException


class VoiceListActivity : BaseSongPlayerActivity(), OnVoiceListAdapterListener {

    companion object {
        private val TAG = VoiceListActivity::class.java.name
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapterVoiceNotes: VoiceNotesAdapter
    private val voiceListViewModel: VoiceListViewModel by viewModels()
    private val recorderViewModel: RecorderViewModel by viewModels()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val extras = intent?.extras
        if (extras != null || extras?.containsKey(RecordingAudio::class.java.name) != true) {
            voiceListViewModel.setRecordState(true)
        }
        extras?.apply {
            if (containsKey(RecordingAudio::class.java.name)) {
                voiceListViewModel.setRecordState(false)
            } else {
//                voiceListViewModel.setRecordState(true)
            }
        }
        recorderViewModel.startAndBindRecordingService()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        App.daggerAppComponent.inject(voiceListViewModel)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setUpRecyclerView()
        observeLiveData()
        setUpTitle()
        setUpListeners()
    }

    override fun onStart() {
        super.onStart()
        voiceListViewModel.getVoiceNotesFromDB()
        //TODO При освобождении юзером приложения из списка задач,
        // единственный способ присоединиться к записывающему сервису после нового запуска -
        // это проверить его наличие в списке запущенных? Иначе как сделать rebind при перезапуске?
        if (recorderViewModel.serviceRecording.value != true
            && isMyServiceRunning(RecordingService::class.java)
        ) {
            recorderViewModel.setServiceRecording(true)
            onNewIntent(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        recorderViewModel.unbindRecordService()
    }

    private fun setUpListeners() {
        binding.fab.setOnClickListener {
            voiceListViewModel.onToggleRecord()
        }
    }

    private fun setUpTitle() {
        supportActionBar?.title =
            getString(R.string.notes) // под каждую ли переменную заводить LiveData?
    }

    private fun showEditNameDialog(defaultName: String) {
        val bindAlert = DialogSetFileNameBinding.inflate(layoutInflater)
        val userInput = bindAlert.editTextDialogUserInput
        val cancel: Button = bindAlert.saveCancel
        val ok: Button = bindAlert.saveOk

        val builder =
            MaterialAlertDialogBuilder(
                this,
                R.style.NotesThemeOverlay_MaterialComponents_MaterialAlertDialog
            )
        builder.setView(bindAlert.root)
        builder.setCancelable(false)
        val dialog = builder.create()

        userInput.hint = defaultName
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        ok.setOnClickListener {
            val newFileName = userInput.text.toString()
            recorderViewModel.changeRecordingName(newFileName)
            dialog.dismiss()
        }

        // create and show the alert dialog
        dialog.show()
    }
    //TODO Заменить на activity result API
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_RECORD -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {// Permission Granted
//                    audioRecorderViewModel.startRecording()
                } else {
                    // Permission Denied
                    showSnackBar(R.string.you_denied_permission)
                }
            }
        }
    }


    private fun setUpRecyclerView() {
        mAdapterVoiceNotes = VoiceNotesAdapter(this)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            this.adapter = mAdapterVoiceNotes
        }
    }

    private fun observeLiveData() {
        audioPlayerViewModel.isPlayData.observe(this) {
            voiceListViewModel.setNotePlayStatus(it)
//            mAdapterVoiceNotes.playingViewHolder?.itemBinding?.playButton?.setImageResource(
//                if (it) R.drawable.ic_pause_vector else R.drawable.ic_play_vector)
        }
        voiceListViewModel.playingState.observe(this) {
            mAdapterVoiceNotes.notes = it.playlist
            if (it.playingNote != null && it.playingNote.isPlaying) toggle(
                it.playlist,
                it.playingNote
            )
        }
        voiceListViewModel.onRecording.observe(this) {
            if (it) {
                if (isRecordPermissionGranted()) {

                    recorderViewModel.startAndBindRecordingService()
                    binding.fab.rotateTo180()

                } else {

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        REQUEST_PERMISSION_RECORD
                    )

                }
            } else {

                stopRecording()

            }
        }
        recorderViewModel.serviceConnected.observe(this) {
            if (it && voiceListViewModel.onRecording.value != true) {
                stopRecording()
            }
        }
        voiceListViewModel.isSyncSuccess.observe(this) {
            if (it)
                showSnackBar(R.string.sync_ok)
        }
        voiceListViewModel.isSyncError.observe(this) {
            if (it) {
                showSnackBar(R.string.sync_error)
                voiceListViewModel.setSyncError(false)
            }
        }
        recorderViewModel.toastMsg.observe(this) {
             showSnackBar(it)
        }
        recorderViewModel.recordingAudio.observe(this) {
            it?.let {
                voiceListViewModel.saveVoiceData(it)
            }
        }
    }

    private fun stopRecording() {
        recorderViewModel.stopRecordingAndService()
        recorderViewModel.recordingAudio.value?.fileName?.let { name ->
            showEditNameDialog(name)
            binding.fab.rotateToDefault()
        }
    }

    private fun showSnackBar(it: Int) {
        val snack = Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
        snack.setAction("ОК") {
            snack.dismiss()
        }
        snack.show()
    }

    override fun toggleNote(note: VoiceNote, voiceNotes: List<VoiceNote>) {
        voiceListViewModel.toggleNote(note)
    }

    override fun removeNoteItem(note: VoiceNote) {
        showRemoveNoteItemConfirmDialog(note)
    }

    private fun showRemoveNoteItemConfirmDialog(note: VoiceNote) {
        // setup the alert builder
        MaterialAlertDialogBuilder(
            this,
            R.style.NotesThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
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
        voiceListViewModel.removeItemFromList(note)
    }

    private fun isRecordPermissionGranted(): Boolean {
        val firstPermissionResult = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
        return firstPermissionResult == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        //val snItem = menu?.findItem(R.id.action_social_network)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_social_network -> {
                VK.login(this, arrayListOf(VKScope.DOCS, VKScope.AUDIO))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {

            override fun onLogin(token: VKAccessToken) {
                // User passed authorization
                Log.i(TAG, "onLogin: success")

                voiceListViewModel.syncVK()

            }

            override fun onLoginFailed(authException: VKAuthException) {
                Log.i(TAG, "onLogin: error")
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

}