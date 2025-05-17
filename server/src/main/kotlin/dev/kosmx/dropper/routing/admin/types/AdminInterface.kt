package dev.kosmx.dropper.routing.admin.types

import io.ktor.resources.Resource

@Resource("list")
class AdminList

@Resource("add")
class AdminAdd(val name: String)

@Resource("delete")
class AdminDelete(val token: String)