package dev.ekvedaras.laravelquery.services.forms;

import com.intellij.database.util.DbUtil
import com.intellij.openapi.project.Project
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.table.JBTable
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableModel


class LaravelQuerySettingsForm(val project: Project) {
    private var panel: JPanel? = null
    private var filterDataSources: JCheckBox? = null
    private var dataSources: JTable? = null

    private val settings: LaravelQuerySettings = LaravelQuerySettings.instance

    fun component(): JComponent? = panel

    fun shouldFilterDataSources() = filterDataSources?.isSelected
    fun filteredDataSources() = dataSources?.selectedRows?.map { dataSources?.getValueAt(it, 0) as String }

    val isModified: Boolean
        get() = shouldFilterDataSources() != settings.filterDataSources

    init {
        loadSettings()
    }

    private fun createUIComponents()
    {
        val model = DefaultTableModel()

        model.addColumn("ID")
        model.addColumn("Data source")
        model.addColumn("Complete")

        DbUtil.getDataSources(project).forEach { dataSource ->
            model.insertRow(0, arrayOf(dataSource.uniqueId, dataSource.toString(), "Maybe"))
        }

//        model.insertRow(0, arrayOf<Any>("DB 1", "Yes"))
//        model.insertRow(0, arrayOf<Any>("DB 2", "No"))

//        model.setValueAt("Value 1", 0, 0)
//        model.setValueAt("Yes", 0, 1)
//
//        model.setValueAt("Value 2", 1, 0)
//        model.setValueAt("No", 1, 1)

        dataSources = JBTable(model)
        dataSources!!.rowHeight = 24
        dataSources!!.autoCreateRowSorter = true
        dataSources!!.columnModel.getColumn(2).cellEditor = JBTable.createBooleanEditor()
        dataSources!!.columnModel.getColumn(2).cellRenderer = BooleanTableCellRenderer()
        dataSources!!.columnModel.getColumn(2).sizeWidthToFit()

        dataSources!!.setSelectionMode(0)

//        val col2 = dataSources.columnModel.getColumn(1)
//        col2.cellEditor = JBTable.createBooleanEditor()
//        col2.cellRenderer = BooleanTableCellRenderer()



//        dataSources = CheckBoxList<String>()
//        dataSources!!.addItem("Item a", "Text b", false)
//        dataSources!!.addItem("Item b", "Text c", true)
    }

    fun loadSettings() {
        filterDataSources?.isSelected = settings.filterDataSources
    }
}
