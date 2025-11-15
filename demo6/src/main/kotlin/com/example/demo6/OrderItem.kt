package com.example.pos

import javafx.beans.property.*

class OrderItem(code: String, desc: String, price: Double, qty: Int) {

    private val codeProp = SimpleStringProperty(code)
    private val descProp = SimpleStringProperty(desc)
    private val priceProp = SimpleDoubleProperty(price)
    private val qtyProp = SimpleIntegerProperty(qty)
    private val amountProp = SimpleDoubleProperty(price * qty)

    // Getters
    fun getCode(): String = codeProp.get()
    fun getDesc(): String = descProp.get()
    fun getPrice(): Double = priceProp.get()
    fun getQty(): Int = qtyProp.get()
    fun getAmount(): Double = amountProp.get()

    // Setters
    fun setCode(value: String) { codeProp.set(value) }
    fun setDesc(value: String) { descProp.set(value) }
    fun setPrice(value: Double) { priceProp.set(value) }
    fun setQty(value: Int) {
        qtyProp.set(value)
        amountProp.set(priceProp.get() * value)
    }
    fun setAmount(value: Double) { amountProp.set(value) }

    // Properties for TableView
    fun codeProperty(): StringProperty = codeProp
    fun descProperty(): StringProperty = descProp
    fun priceProperty(): DoubleProperty = priceProp
    fun qtyProperty(): IntegerProperty = qtyProp
    fun amountProperty(): DoubleProperty = amountProp
}
