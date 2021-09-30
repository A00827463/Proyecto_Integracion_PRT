package mx.itesm.testbasicapi.model.repository.responseinterface

import mx.itesm.testbasicapi.model.entities.Product

interface IGetProducts : IBasicResponse {
    fun onSuccess(products: List<Product>?)
}