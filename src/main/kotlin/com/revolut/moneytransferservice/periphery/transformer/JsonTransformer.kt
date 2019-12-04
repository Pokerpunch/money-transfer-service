package com.revolut.moneytransferservice.periphery.transformer

import com.google.gson.Gson
import spark.ResponseTransformer

class JsonTransformer : ResponseTransformer { // TODO delete this guy?

    private val gson = Gson()

    override fun render(model: Any): String = gson.toJson(model)

    fun json(): (Any) -> String = this::render
}