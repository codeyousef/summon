package code.yousef.summon.examples.js.i18n

import code.yousef.summon.examples.js.models.Language

object Translations {
    private val translations = mapOf(
        Language.ENGLISH to mapOf(
            // App
            "app.title" to "Todo App",
            "app.subtitle" to "Manage your tasks efficiently",
            
            // Auth
            "auth.login" to "Login",
            "auth.register" to "Register",
            "auth.username" to "Username",
            "auth.email" to "Email",
            "auth.password" to "Password",
            "auth.logout" to "Logout",
            "auth.welcome" to "Welcome",
            "auth.remember_me" to "Remember me",
            
            // Todo
            "todo.add_placeholder" to "What needs to be done?",
            "todo.add_button" to "Add Todo",
            "todo.edit" to "Edit",
            "todo.delete" to "Delete",
            "todo.save" to "Save",
            "todo.cancel" to "Cancel",
            "todo.mark_complete" to "Mark as complete",
            "todo.mark_incomplete" to "Mark as incomplete",
            "todo.clear_completed" to "Clear completed",
            "todo.items_left" to "items left",
            "todo.item_left" to "item left",
            
            // Filters
            "filter.all" to "All",
            "filter.active" to "Active",
            "filter.completed" to "Completed",
            
            // Theme
            "theme.light" to "Light",
            "theme.dark" to "Dark",
            "theme.toggle" to "Toggle theme",
            
            // Language
            "language.select" to "Language",
            
            // Messages
            "message.no_todos" to "No todos yet. Add one above!",
            "message.no_active" to "No active todos",
            "message.no_completed" to "No completed todos",
            "message.login_required" to "Please login to continue",
            "message.login_success" to "Welcome back!",
            "message.logout_success" to "Goodbye!"
        ),
        
        Language.SPANISH to mapOf(
            // App
            "app.title" to "Lista de Tareas",
            "app.subtitle" to "Gestiona tus tareas eficientemente",
            
            // Auth
            "auth.login" to "Iniciar Sesión",
            "auth.register" to "Registrarse",
            "auth.username" to "Usuario",
            "auth.email" to "Correo",
            "auth.password" to "Contraseña",
            "auth.logout" to "Cerrar Sesión",
            "auth.welcome" to "Bienvenido",
            "auth.remember_me" to "Recordarme",
            
            // Todo
            "todo.add_placeholder" to "¿Qué necesitas hacer?",
            "todo.add_button" to "Agregar Tarea",
            "todo.edit" to "Editar",
            "todo.delete" to "Eliminar",
            "todo.save" to "Guardar",
            "todo.cancel" to "Cancelar",
            "todo.mark_complete" to "Marcar como completada",
            "todo.mark_incomplete" to "Marcar como pendiente",
            "todo.clear_completed" to "Limpiar completadas",
            "todo.items_left" to "elementos restantes",
            "todo.item_left" to "elemento restante",
            
            // Filters
            "filter.all" to "Todas",
            "filter.active" to "Activas",
            "filter.completed" to "Completadas",
            
            // Theme
            "theme.light" to "Claro",
            "theme.dark" to "Oscuro",
            "theme.toggle" to "Cambiar tema",
            
            // Language
            "language.select" to "Idioma",
            
            // Messages
            "message.no_todos" to "No hay tareas aún. ¡Agrega una arriba!",
            "message.no_active" to "No hay tareas activas",
            "message.no_completed" to "No hay tareas completadas",
            "message.login_required" to "Por favor inicia sesión para continuar",
            "message.login_success" to "¡Bienvenido de vuelta!",
            "message.logout_success" to "¡Hasta luego!"
        ),
        
        Language.FRENCH to mapOf(
            // App
            "app.title" to "Liste de Tâches",
            "app.subtitle" to "Gérez vos tâches efficacement",
            
            // Auth
            "auth.login" to "Se connecter",
            "auth.register" to "S'inscrire",
            "auth.username" to "Nom d'utilisateur",
            "auth.email" to "Email",
            "auth.password" to "Mot de passe",
            "auth.logout" to "Se déconnecter",
            "auth.welcome" to "Bienvenue",
            "auth.remember_me" to "Se souvenir de moi",
            
            // Todo
            "todo.add_placeholder" to "Que faut-il faire ?",
            "todo.add_button" to "Ajouter Tâche",
            "todo.edit" to "Modifier",
            "todo.delete" to "Supprimer",
            "todo.save" to "Sauvegarder",
            "todo.cancel" to "Annuler",
            "todo.mark_complete" to "Marquer comme terminée",
            "todo.mark_incomplete" to "Marquer comme en cours",
            "todo.clear_completed" to "Effacer terminées",
            "todo.items_left" to "éléments restants",
            "todo.item_left" to "élément restant",
            
            // Filters
            "filter.all" to "Toutes",
            "filter.active" to "Actives",
            "filter.completed" to "Terminées",
            
            // Theme
            "theme.light" to "Clair",
            "theme.dark" to "Sombre",
            "theme.toggle" to "Changer thème",
            
            // Language
            "language.select" to "Langue",
            
            // Messages
            "message.no_todos" to "Aucune tâche pour le moment. Ajoutez-en une ci-dessus !",
            "message.no_active" to "Aucune tâche active",
            "message.no_completed" to "Aucune tâche terminée",
            "message.login_required" to "Veuillez vous connecter pour continuer",
            "message.login_success" to "Bon retour !",
            "message.logout_success" to "Au revoir !"
        )
    )
    
    fun get(key: String, language: Language): String {
        return translations[language]?.get(key) ?: key
    }
}