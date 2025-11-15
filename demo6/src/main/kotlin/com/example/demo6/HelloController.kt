package com.example.demo6

import com.example.pos.Item
import com.example.pos.OrderItem
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.converter.IntegerStringConverter

class HelloController {

    @FXML private lateinit var RYes: RadioButton
    @FXML private lateinit var RNO: RadioButton
    @FXML private lateinit var Discount: TextField
    @FXML private lateinit var ItemCode: TextField
    @FXML private lateinit var TAmount: TextField
    @FXML private lateinit var AmountPay: TextField

    @FXML private lateinit var table: TableView<OrderItem>
    @FXML private lateinit var Itemcode: TableColumn<OrderItem, String>
    @FXML private lateinit var Desc: TableColumn<OrderItem, String>
    @FXML private lateinit var UnitP: TableColumn<OrderItem, Double>
    @FXML private lateinit var QTY: TableColumn<OrderItem, Int>
    @FXML private lateinit var Amount: TableColumn<OrderItem, Double>

    private val itemDatabase = HashMap<String, Item>()
    private var discountRate = 0.0

    @FXML
    fun initialize() {

        table.isEditable = true

        // Item list
        itemDatabase["101"] = Item("101", "Kapeng Tagalog/Ilokano", 8.25)
        itemDatabase["102"] = Item("102", "Asukal na Sugar", 82.75)
        itemDatabase["103"] = Item("103", "Creamer na Gatas", 5.00)
        itemDatabase["104"] = Item("104", "Tinapay na Bread", 65.00)
        itemDatabase["105"] = Item("105", "Chips na chichirya", 8.00)

        // Table column binding
        Itemcode.setCellValueFactory { it.value.codeProperty() }
        Desc.setCellValueFactory { it.value.descProperty() }
        UnitP.setCellValueFactory { it.value.priceProperty().asObject() }
        QTY.setCellValueFactory { it.value.qtyProperty().asObject() }
        Amount.setCellValueFactory { it.value.amountProperty().asObject() }

        // Enable quantity column editing
        QTY.cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
        QTY.setOnEditCommit { event ->
            val row = event.rowValue
            var newQty = event.newValue
            if (newQty <= 0) newQty = 1

            row.setQty(newQty)
            updateTotals()
        }

        // Edit item code directly in table
        Itemcode.cellFactory = TextFieldTableCell.forTableColumn()
        Itemcode.setOnEditCommit { event ->
            val row = event.rowValue
            val newCode = event.newValue.trim()
            val item = itemDatabase[newCode]

            if (item == null) {
                Alert(Alert.AlertType.ERROR, "Item code does not exist: $newCode").show()
                table.refresh()
                return@setOnEditCommit
            }

            // If duplicate item → merge quantity
            for (existing in table.items) {
                if (existing != row && existing.getCode() == newCode) {
                    existing.setQty(existing.getQty() + row.getQty())
                    table.items.remove(row)
                    updateTotals()
                    return@setOnEditCommit
                }
            }

            // Otherwise update info
            row.setCode(item.code)
            row.setDesc(item.desc)
            row.setPrice(item.price)
            row.setAmount(item.price * row.getQty())
            updateTotals()
        }

        // Recalculate totals when table changes
        table.items.addListener(ListChangeListener { updateTotals() })
    }

    @FXML
    fun onAddClick() {
        val code = ItemCode.text.trim()
        if (code.isEmpty()) {
            Alert(Alert.AlertType.WARNING, "Please enter an item code.").show()
            return
        }

        val item = itemDatabase[code]
        if (item == null) {
            Alert(Alert.AlertType.ERROR, "Item code not found: $code").show()
            return
        }

        // If item already exists in table
        for (row in table.items) {
            if (row.getCode() == code) {
                row.setQty(row.getQty() + 1)
                updateTotals()
                ItemCode.clear()
                return
            }
        }

        // Add new row
        table.items.add(OrderItem(item.code, item.desc, item.price, 1))
        ItemCode.clear()
        updateTotals()
    }

    @FXML
    fun onRemoveClick() {
        val selected = table.selectionModel.selectedItem
        if (selected != null) {
            table.items.remove(selected)
            updateTotals()
        } else {
            Alert(Alert.AlertType.INFORMATION, "Select a row to remove.").show()
        }
    }

    @FXML
    fun onYesClick(e: ActionEvent) {
        discountRate = 0.10
        Discount.text = "10%"
        updateTotals()
    }

    @FXML
    fun onNoClick(e: ActionEvent) {
        discountRate = 0.0
        Discount.text = "0%"
        updateTotals()
    }

    private fun updateTotals() {
        var total = 0.0
        for (row in table.items) {
            total += row.getAmount()
        }

        TAmount.text = String.format("%.2f", total)

        val discounted = total * (1 - discountRate)
        AmountPay.text = String.format("%.2f", discounted)
    }
}
