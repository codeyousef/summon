package code.yousef.example.portfolio.routes

import code.yousef.example.portfolio.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.apiRoutes() {
    val portfolioRepository by inject<PortfolioRepository>()
    val categoryRepository by inject<CategoryRepository>()
    
    route("/api") {
        // Projects API
        route("/projects") {
            get {
                val projects = portfolioRepository.getPublishedProjects()
                call.respond(projects)
            }
            
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val project = portfolioRepository.getProjectById(id)
                if (project != null) {
                    call.respond(project)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        
        // Journal API
        route("/journal") {
            get {
                val entries = portfolioRepository.getPublishedJournalEntries()
                call.respond(entries)
            }
            
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val entry = portfolioRepository.getJournalEntryById(id)
                if (entry != null) {
                    call.respond(entry)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        
        // Categories API
        route("/categories") {
            get {
                val categories = categoryRepository.getAllCategories()
                call.respond(categories)
            }
            
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val category = categoryRepository.getCategoryById(id)
                if (category != null) {
                    call.respond(category)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}