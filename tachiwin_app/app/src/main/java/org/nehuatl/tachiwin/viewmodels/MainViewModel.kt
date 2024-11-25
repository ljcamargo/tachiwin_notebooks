package org.nehuatl.tachiwin.viewmodels

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.nehuatl.llamacpp.LlamaAndroid
import org.nehuatl.llamacpp.LlamaHelper
import org.nehuatl.tachiwin.BuildConfig
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.LlamaInstruct
import org.nehuatl.tachiwin.core.Logger
import org.nehuatl.tachiwin.core.Preferences
import org.nehuatl.tachiwin.models.Categories
import org.nehuatl.tachiwin.models.Dictionary
import org.nehuatl.tachiwin.models.Downloadable
import org.nehuatl.tachiwin.models.Entries
import org.nehuatl.tachiwin.models.Entry
import org.nehuatl.tachiwin.models.Query
import org.nehuatl.tachiwin.models.Report
import org.nehuatl.tachiwin.network.RemoteClient
import org.nehuatl.tachiwin.network.ReportClient
import org.nehuatl.tachiwin.ui.theme.ThemeSpecs
import org.nehuatl.tachiwin.v
import java.io.File
import java.util.Locale


open class MainViewModel(): ViewModel(), KoinComponent {

    val logger: Logger by inject()
    private val bearer: ContextBearer by inject()
    private val llamaHelper by lazy { LlamaHelper(heavyScope) }
    private val preferences: Preferences by inject()
    private val supervisor = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Main + supervisor)
    private val heavyScope = CoroutineScope(Dispatchers.IO + supervisor)
    private val extFilesDir by lazy { bearer.context.getExternalFilesDir(null) }

    open val status = MutableStateFlow(UpdateState.IDLE)
    open val wasDark = MutableLiveData(false)
    open val firstCharIndex = MutableLiveData(-1)
    open val alt = MutableLiveData(false)

    open val isFiltered = MutableLiveData<Boolean>()
    open val entry = MutableLiveData<Entry>()

    val _dictionaries = Dictionary.startDictionaries(
        context = bearer.context,
        file = "dictionaries.json"
    )
    val dictionaries = MutableLiveData(_dictionaries)
    private val _dictionary = MutableLiveData(_dictionaries.first())
    val dictionary: LiveData<Dictionary> = _dictionary

    private val _categories = MutableLiveData(_dictionaries.first().categories ?: listOf())
    val categories: LiveData<Categories> = _categories

    private val _entries = MutableLiveData(_dictionaries.first().entries)
    val entries: LiveData<Entries> = _entries

    private val _reportFinished = MutableSharedFlow<Boolean?>()
    val reportFinished: SharedFlow<Boolean?> = _reportFinished.asSharedFlow()

    private val _predictedText = MutableStateFlow("")
    val predictedText: StateFlow<String> = _predictedText

    open val scheme = MutableLiveData(_dictionaries.first().uid)
    open val schemePair = MutableLiveData(ThemeSpecs.DefaultPair)
    open val schemeP = MutableStateFlow(ThemeSpecs.Default)

    private val _modelLoaded = MutableStateFlow(false)
    val modelLoaded: StateFlow<Boolean> = _modelLoaded

    private var _modelState = MutableStateFlow<UIState>(UIState.Idle)
    val modelState: StateFlow<UIState> = _modelState

    private var _inferenceMode = MutableStateFlow(preferences.inferenceMode)
    val inferenceMode: StateFlow<Preferences.InferenceMode?> = _inferenceMode

    private var _uiLanguage = MutableStateFlow(preferences.currentLanguage())
    val uiLanguage: StateFlow<String?> = _uiLanguage

    private var _lightMode = MutableStateFlow(preferences.lightMode)
    val lightMode: StateFlow<Preferences.LightMode> = _lightMode

    private val llama by lazy { LlamaAndroid() }
    private var job: Job?= null
    private val modelContext = MutableStateFlow<Int?>(null)

    val translateQuery = MutableStateFlow(Query.empty())
    val dictionaryQuery = MutableStateFlow(Query.empty())
    open val results = dictionaryQuery
        .debounce(DEBOUNCE_LATENCY)
        .flatMapLatest {
            flowOf(findFlow(it))
        }.onCompletion {
            "completed".v()
            status.value = UpdateState.IDLE
        }
    private var lastQuery = Query.empty()

    private fun filteredEntries(query: Query): Entries {
        return if (query.text.isNotEmpty()) {
            isFiltered.value = true
            dictionary.value?.find(query.text, query.filters)
        } else {
            if (query.filters.isEmpty()) {
                isFiltered.value = false
                dictionary.value?.all()
            } else {
                isFiltered.value = true
                dictionary.value?.allFiltered(query.filters)
            }
        } ?: listOf()
    }

    open suspend fun findFlow(query: Query) = filteredEntries(query).also {
        if (lastQuery != query) {
            lastQuery = query
            status.value = UpdateState.RESET
        }
        _entries.value = it
    }

    open fun toggleFilter(filterId: String) = scope.launch {
        _categories.value = _categories.value
            ?.map {
                if (it.uid == filterId) it.selected = it.selected.not()
                it
            }
            ?.also {
                dictionaryQuery.tryEmit(
                    lastQuery.copy(
                        filters = it
                            .filter { it.selected }
                            .map { it.uid }
                    )
                )
            }
    }

    open fun find(text: String) {
        status.value = UpdateState.WORKING
        logger.search()
        dictionaryQuery.tryEmit(
            Query(
                text = text,
                dictionary = dictionary.value!!.uid,
                filters = categories.value?.filter { it.selected }?.map { it.uid } ?: listOf()
            )
        )
    }

    open fun any(): Entry {
        return dictionary.value!!.any()
    }

    open fun findEntry(uid: String) = entry loadFrom {
        dictionary.value!!.entry(uid).also {
            it?.findAnnotations()
        }
    }

    fun startDictionary() = load {
        _dictionary.value?.also {
            it.loadEntries(bearer.context).let {
                _entries.value = it
            }
        }
    }

    fun setDictionary(uid: String) = load {
        if (uid == dictionary.value?.uid) return@load
        _dictionary.value?.also { it.unloadEntries() }
        _entries.value = listOf()
        _dictionary.value = _dictionaries
            .firstOrNull { it.uid == uid }
            ?.also {
                it.loadEntries(bearer.context).let {
                    _entries.value = it
                }
                _categories.value = it.categories
                scheme.value = it.uid
                schemePair.value = ThemeSpecs.findScheme(it.uid)
                refreshTheme()
                find(dictionaryQuery.value.text)
            }
    }

    open fun characters(): List<String> {
        return dictionary.value!!.characters() ?: listOf()
    }

    open fun charIndexToScrollPosition(index: Int) {
        val char = characters().getOrNull(index) ?: return
        val itemIndex = entries.value?.indexOfFirst {
            it.isSectionHeader && it.header?.title.equals(char, ignoreCase = true)
        }
        firstCharIndex.postValue(itemIndex ?: -1)
    }

    open fun visibleItemToCharIndex(index: Int): Float? {
        if (index == 0) return 0f
        val entries2 = entries.value ?: return null
        entries2.getOrNull(index) ?: return null
        val char:String = entries2.subList(0, index+1).lastOrNull {
            it.isSectionHeader
        }?.header?.title ?: return null
        val charIndex = characters().indexOfFirst { it.lowercase() == char }
        return if (charIndex != -1) charIndex.toFloat() else null
    }

    open fun refreshTheme(systemInDarkTheme: Boolean = wasDark.value ?: false) = load {
        val lightMode = preferences.lightMode
        wasDark.value = systemInDarkTheme
        val (light, dark) = schemePair.value ?: ThemeSpecs.DefaultPair
        val theme = when (lightMode) {
            Preferences.LightMode.AUTO -> if (systemInDarkTheme) dark else light
            Preferences.LightMode.LIGHT -> light
            Preferences.LightMode.DARK -> dark
        }
        schemeP.emit(theme)
    }

    open fun didScroll() {
        status.value = UpdateState.IDLE
    }

    open fun writeTranslate(text: String) {
        translateQuery.tryEmit(
            Query(
                text = text,
                dictionary = dictionary.value!!.uid,
                filters = listOf()
            )
        )
    }

    private fun Query.prompt(): String? {
        val dict = this@MainViewModel.dictionary.value ?: return null
        val source = dict.language1.iso3.lowercase()
        val target = dict.language2.iso3.lowercase()
        val instruction = "Translate from ($source) to ($target)"
        return LlamaInstruct.prompt(instruction, text)
    }

    open fun translate() {
        val query = translateQuery.value
        if (query.text.isEmpty()) return
        "translate mode ${_inferenceMode.value}".v()
        "translate mode ${preferences.inferenceMode}".v()
        when (_inferenceMode.value) {
            Preferences.InferenceMode.REMOTE -> {
                _modelLoaded.value = true
                try {
                    job = heavyScope.launch { remoteSend() }
                } catch (exc: IllegalStateException) {
                    "error loading model $exc".v()
                    job?.cancel()
                }
            }
            Preferences.InferenceMode.LOCAL -> {
                if (_modelLoaded.value.not()) {
                    loadLocalModelAndSend()
                } else {
                    try {
                        job = heavyScope.launch { localSend() }
                    } catch (exc: IllegalStateException) {
                        "error loading model $exc".v()
                        job?.cancel()
                    }
                }
            }
            else -> return
        }
    }

    private fun loadLocalModelAndSend() {
        "loading model".v()
        _modelState.value = UIState.Loading
        _modelLoaded.value = false
        try {
            job = heavyScope.launch {
                "loading model in".v()
                val map = llama.initContext(
                    mapOf(
                        "model" to model.destination.absolutePath,
                        "n_ctx" to 2048,
                    )
                )
                "model initialized $map".v()
                map?.get("contextId")?.let {
                    "got context id $it".v()
                    modelContext.value = it as? Int
                    _modelLoaded.value = true
                    localSend()
                }
            }
        } catch (exc: IllegalStateException) {
            "error loading model $exc".v()
            load {
                unload()
                "load() failed $exc".v()
            }
        }
    }

    private fun localSend() {
        val text = translateQuery.value.prompt() ?: return
        "llama send $text".v()
        _modelState.value = UIState.Streaming
        val context = modelContext.value ?: return
        job = heavyScope.launch {
            "setting event collector".v()
            llama.setEventCollector(context, this)
                .onStart {
                    "event collector start".v()
                    _modelState.value = UIState.Streaming
                    _predictedText.value = ""
                }
                .onCompletion {
                    "completed, all message $it".v()
                    llama.unsetEventCollector(context)
                    _modelState.value = UIState.Idle
                }
                .mapNotNull { (kind, value) ->
                    if (kind == "token") value else null
                }
                .collect {
                    "receiving chunk $it".v()
                    _predictedText.value += it
                }
        }
        "event collector LAUNCH".v()
        llama.launchCompletion(context, mapOf(
            "prompt" to text,
            "emit_partial_completion" to true,
        )).also {
            "finished launchCompletion? $it".v()
            job?.cancel()
        }
    }

    private fun remoteSendStream() {
        val text = translateQuery.value.prompt() ?: return
        val client = RemoteClient()
        viewModelScope.launch {
            _modelState.value = UIState.Streaming
            _predictedText.value = ""
            try {
                client.sendStream(text, _predictedText)
            } catch (e: Exception) {
                println("Error: ${e.message}")
                _modelState.value = UIState.Error
            } finally {
                _modelState.value = UIState.Idle
            }
        }
    }

    private fun remoteSend() {
        val text = translateQuery.value.prompt() ?: return
        "translating remote $text".v()
        val client = RemoteClient()
        viewModelScope.launch {
            _modelState.value = UIState.Streaming
            _predictedText.value = "" // Clear previous predictions
            try {
                client.send(prompt = text, _predictedText)
            } catch (e: Exception) {
                // Handle network or other exceptions
                println("Error: ${e.message}")
                _modelState.value = UIState.Error
            } finally {
                _modelState.value = UIState.Idle
            }
        }
    }

    private fun unload() = scope.launch {
        modelContext.value?.let { llama.releaseContext(it) }
        modelContext.emit(null)
        _modelState.value = UIState.Idle
        if (_modelState.value === UIState.Streaming) abort()
    }

    fun abort() = scope.launch {
        job?.cancel()
        val context = modelContext.value ?: return@launch
        llama.stopCompletion(context)
    }

    fun reportTranslation() = scope.launch {
        val text = translateQuery.value.prompt() ?: return@launch
        val translated = _predictedText.value
        val dictionaryPair = dictionary.value?.uid
        val report = Report(
            date = Clock.System.now(),
            version = BuildConfig.VERSION_NAME,
            comment = "$dictionaryPair: $text -> $translated",
        )
        _reportFinished.emit(ReportClient.issueReport(report, scope))
    }

    fun setInferenceMode(mode: Preferences.InferenceMode) {
        preferences.inferenceMode = mode
        _inferenceMode.value = mode
        _predictedText.value = ""
    }

    fun setUILanguageIndex(lang: Int) {
        preferences.setLanguageIndex(lang)?.let {
            _uiLanguage.value = it
            "language is now $it".v()
        }
    }

    fun setLightMode(mode: Preferences.LightMode) {
        preferences.lightMode = mode
        _lightMode.value = mode
        refreshTheme()
    }

    override fun onCleared() {
        super.onCleared()
        llamaHelper.abort()
        llamaHelper.release()
    }

    val model = Downloadable(
        name = Constants.STT_MODEL_NAME,
        source = Uri.parse(Constants.STT_MODEL_URL),
        destination = File(extFilesDir, Constants.STT_MODEL_FILENAME)
    )

    enum class UpdateState {
        IDLE, WORKING, RESET
    }

    sealed class UIState {
        object Idle : UIState()
        object Loading : UIState()
        object Streaming : UIState()
        object Error : UIState()
    }

    infix fun<T> MutableLiveData<T>.loadFrom(apiCall: suspend () -> T?) = load {
        postValue(apiCall())
    }

    fun load(calls: suspend () -> Unit) = scope.launch {
        calls()
    }

    companion object {
        const val DEBOUNCE_LATENCY = 600L
    }

}