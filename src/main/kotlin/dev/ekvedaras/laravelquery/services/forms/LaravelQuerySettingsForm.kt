package dev.ekvedaras.laravelquery.services.forms;

import com.intellij.database.psi.DbDataSource
import com.intellij.database.util.DbUtil
import com.intellij.openapi.project.Project
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.table.JBTable
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableModel


class LaravelQuerySettingsForm(val project: Project) {
    private var panel: JPanel? = null
    private var filterDataSources: JCheckBox? = null
    private var dataSources: JTable? = null

    private val settings: LaravelQuerySettings = LaravelQuerySettings.getInstance(project)

    fun component(): JComponent? = panel

    fun shouldFilterDataSources() = filterDataSources?.isSelected
    fun filteredDataSources(): List<String> {
        var selected = listOf<String>()

        for (row in 0 until (dataSources?.rowCount ?: 0)) {
            val dataSource = dataSources?.getValueAt(row, 1) as DbDataSource

            if (dataSources?.getValueAt(row, 0) == true) {
                selected = selected + dataSource.uniqueId
            }
        }

        return selected
    }

    val isModified: Boolean
        get() = shouldFilterDataSources() != settings.filterDataSources ||
            filteredDataSources() != settings.filteredDataSources

    init {
        loadSettings()
    }

    private fun createUIComponents() {
        val model = object : DefaultTableModel() {
            override fun isCellEditable(row: Int, column: Int) = column == 0
        }

        model.addColumn("Complete")
        model.addColumn("Data source")

        DbUtil.getDataSources(project).forEach { dataSource ->
            model.insertRow(0, arrayOf(false, dataSource))
        }

        dataSources = JBTable(model)
        dataSources!!.isEnabled = false
        dataSources!!.addMouseListener(
            object : MouseAdapter() {
                override fun mouseClicked(event: MouseEvent) {
                    val row = dataSources!!.rowAtPoint(event.point)

                    if (row >= 0 && dataSources!!.isEnabled) {
                        dataSources!!.setValueAt(
                            !(dataSources!!.getValueAt(row, 0) as Boolean),
                            row,
                            0
                        )
                    }
                }
            }
        )

        dataSources!!.columnModel.getColumn(0).cellEditor = JBTable.createBooleanEditor()
        dataSources!!.columnModel.getColumn(0).cellRenderer = BooleanTableCellRenderer()
        dataSources!!.columnModel.getColumn(0).preferredWidth = 60
        dataSources!!.columnModel.getColumn(0).resizable = false

        dataSources!!.columnModel.getColumn(1).preferredWidth = 500


        filterDataSources = JBCheckBox()
        filterDataSources!!.addChangeListener {
            dataSources!!.isEnabled = filterDataSources!!.isSelected
        }
    }

    fun loadSettings() {
        filterDataSources?.isSelected = settings.filterDataSources
        dataSources?.isEnabled = filterDataSources?.isSelected ?: false

        for (row in 0 until (dataSources?.rowCount ?: 0)) {
            val dataSource = dataSources?.getValueAt(row, 1) as DbDataSource
            dataSources?.setValueAt(settings.filteredDataSources.contains(dataSource.uniqueId), row, 0)
        }
    }
}
