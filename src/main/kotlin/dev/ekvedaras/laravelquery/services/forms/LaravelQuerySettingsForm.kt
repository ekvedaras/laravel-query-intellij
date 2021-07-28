package dev.ekvedaras.laravelquery.services.forms;

import com.intellij.ui.table.JBTable
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.ListSelectionModel


class LaravelQuerySettingsForm {
    private var panel : JPanel? = null
    private var filterDataSources : JCheckBox? = null
    private var dataSources : JTable? = null

    private val settings: LaravelQuerySettings = LaravelQuerySettings.instance

    fun component(): JComponent? = panel

    fun shouldFilterDataSources() = filterDataSources?.isSelected
    fun filteredDataSources() = dataSources?.selectedRows

    val isModified : Boolean
        get() = shouldFilterDataSources() != settings.filterDataSources

    init {
        loadSettings()
    }

    fun loadSettings() {
        filterDataSources?.isSelected = settings.filterDataSources

        val model = ListTableModel<ColumnInfo<String?, String?>>(
            object : ColumnInfo<String?, String?>("Data Source") {
                override fun valueOf(o: String?): String? {
                    return o
                }
            }
        )

        dataSources = JBTable(model)
        dataSources!!.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        dataSources!!.model.setValueAt("One", 0, 0)
    }
}
