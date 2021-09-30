package mx.itesm.testbasicapi.model.repository.responseinterface

import mx.itesm.testbasicapi.model.entities.Product

interface IGetProduct:IBasicResponse {
    fun onSuccess(product: Product?)
}