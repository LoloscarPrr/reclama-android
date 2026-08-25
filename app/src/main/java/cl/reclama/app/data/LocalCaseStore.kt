package cl.reclama.app.data

import android.content.Context
import cl.reclama.app.domain.CaseCategory
import cl.reclama.app.domain.CaseStatus
import cl.reclama.app.domain.ConsumerCase
import org.json.JSONArray
import org.json.JSONObject

class LocalCaseStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadCases(): List<ConsumerCase> {
        val raw = preferences.getString(KEY_CASES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        ConsumerCase(
                            id = item.getString("id"),
                            title = item.getString("title"),
                            company = item.optString("company").takeIf { it.isNotBlank() },
                            category = CaseCategory.valueOf(item.getString("category")),
                            status = CaseStatus.valueOf(item.optString("status", CaseStatus.DRAFT.name)),
                            narrative = item.optString("narrative").takeIf { it.isNotBlank() }
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveCases(cases: List<ConsumerCase>) {
        val array = JSONArray()
        cases.forEach { consumerCase ->
            array.put(
                JSONObject().apply {
                    put("id", consumerCase.id)
                    put("title", consumerCase.title)
                    put("company", consumerCase.company.orEmpty())
                    put("category", consumerCase.category.name)
                    put("status", consumerCase.status.name)
                    put("narrative", consumerCase.narrative.orEmpty())
                }
            )
        }
        preferences.edit().putString(KEY_CASES, array.toString()).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "reclama_local_cases"
        const val KEY_CASES = "cases"
    }
}
