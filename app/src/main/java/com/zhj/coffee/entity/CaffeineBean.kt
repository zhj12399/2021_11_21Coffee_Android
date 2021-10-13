package com.zhj.coffee.entity

import java.sql.Timestamp

class CaffeineBean(
    var id: String,
    var time: String,
    var brand: String,
    var type: String,
    var size: String,
    var percent: Float,
    var caffeine: Float
)