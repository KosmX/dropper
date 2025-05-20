package dev.kosmx.dropper.resources

import io.ktor.resources.Resource

@Resource("list")
class AdminList

@Resource("add")
class AdminAdd(val name: String)

@Resource("delete")
class AdminDelete(val token: String)