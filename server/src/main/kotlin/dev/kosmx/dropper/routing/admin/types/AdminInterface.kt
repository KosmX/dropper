package dev.kosmx.dropper.routing.admin.types

import io.ktor.resources.Resource

@Resource("admin")
class Admin {
    @Resource("list")
    class List(val parent: Admin = Admin())

    @Resource("add")
    class Add(val parent: Admin = Admin(), name: String? = null, token: String)

    @Resource("delete")
    class Delete(val parent: Admin = Admin(), token: String)
}