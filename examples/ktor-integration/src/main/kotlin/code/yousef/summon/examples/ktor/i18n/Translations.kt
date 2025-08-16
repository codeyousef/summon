package code.yousef.summon.examples.ktor.i18n

import code.yousef.summon.examples.ktor.models.Language

object Translations {
    private val translations = mapOf(
        Language.ENGLISH to mapOf(
            // App
            "app.title" to "Ktor Todo App",
            "app.subtitle" to "Real-time collaborative todo management",
            "app.powered_by" to "Powered by Summon Framework",
            
            // Auth
            "auth.login" to "Login",
            "auth.register" to "Register",
            "auth.username" to "Username",
            "auth.email" to "Email",
            "auth.password" to "Password",
            "auth.logout" to "Logout",
            "auth.welcome" to "Welcome",
            "auth.remember_me" to "Remember me",
            "auth.login_success" to "Login successful!",
            "auth.register_success" to "Registration successful!",
            "auth.login_failed" to "Invalid username or password",
            "auth.register_failed" to "Registration failed. Username or email may already exist.",
            
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
            "todo.real_time" to "Real-time updates enabled",
            
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
            "message.todo_added" to "Todo added successfully",
            "message.todo_updated" to "Todo updated successfully",
            "message.todo_deleted" to "Todo deleted successfully",
            "message.todos_cleared" to "Completed todos cleared",
            
            // Real-time
            "realtime.user_joined" to "joined the app",
            "realtime.user_left" to "left the app",
            "realtime.todo_added_by" to "added a todo",
            "realtime.todo_updated_by" to "updated a todo",
            "realtime.todo_deleted_by" to "deleted a todo",
            "realtime.connected_users" to "Connected users",
            
            // Errors
            "error.server_error" to "Server error occurred",
            "error.network_error" to "Network error. Please try again.",
            "error.todo_not_found" to "Todo not found",
            "error.unauthorized" to "Unauthorized access"
        ),
        
        Language.SPANISH to mapOf(
            // App
            "app.title" to "Aplicación Ktor Todo",
            "app.subtitle" to "Gestión colaborativa de tareas en tiempo real",
            "app.powered_by" to "Impulsado por Summon Framework",
            
            // Auth
            "auth.login" to "Iniciar Sesión",
            "auth.register" to "Registrarse",
            "auth.username" to "Usuario",
            "auth.email" to "Correo",
            "auth.password" to "Contraseña",
            "auth.logout" to "Cerrar Sesión",
            "auth.welcome" to "Bienvenido",
            "auth.remember_me" to "Recordarme",
            "auth.login_success" to "¡Inicio de sesión exitoso!",
            "auth.register_success" to "¡Registro exitoso!",
            "auth.login_failed" to "Usuario o contraseña inválidos",
            "auth.register_failed" to "Registro fallido. El usuario o email puede ya existir.",
            
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
            "todo.real_time" to "Actualizaciones en tiempo real habilitadas",
            
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
            "message.todo_added" to "Tarea agregada exitosamente",
            "message.todo_updated" to "Tarea actualizada exitosamente",
            "message.todo_deleted" to "Tarea eliminada exitosamente",
            "message.todos_cleared" to "Tareas completadas limpiadas",
            
            // Real-time
            "realtime.user_joined" to "se unió a la aplicación",
            "realtime.user_left" to "salió de la aplicación",
            "realtime.todo_added_by" to "agregó una tarea",
            "realtime.todo_updated_by" to "actualizó una tarea",
            "realtime.todo_deleted_by" to "eliminó una tarea",
            "realtime.connected_users" to "Usuarios conectados",
            
            // Errors
            "error.server_error" to "Ocurrió un error del servidor",
            "error.network_error" to "Error de red. Por favor intenta de nuevo.",
            "error.todo_not_found" to "Tarea no encontrada",
            "error.unauthorized" to "Acceso no autorizado"
        ),
        
        Language.FRENCH to mapOf(
            // App
            "app.title" to "Application Ktor Todo",
            "app.subtitle" to "Gestion collaborative de tâches en temps réel",
            "app.powered_by" to "Alimenté par Summon Framework",
            
            // Auth
            "auth.login" to "Se connecter",
            "auth.register" to "S'inscrire",
            "auth.username" to "Nom d'utilisateur",
            "auth.email" to "Email",
            "auth.password" to "Mot de passe",
            "auth.logout" to "Se déconnecter",
            "auth.welcome" to "Bienvenue",
            "auth.remember_me" to "Se souvenir de moi",
            "auth.login_success" to "Connexion réussie !",
            "auth.register_success" to "Inscription réussie !",
            "auth.login_failed" to "Nom d'utilisateur ou mot de passe invalide",
            "auth.register_failed" to "Inscription échouée. Le nom d'utilisateur ou l'email peut déjà exister.",
            
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
            "todo.real_time" to "Mises à jour en temps réel activées",
            
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
            "message.todo_added" to "Tâche ajoutée avec succès",
            "message.todo_updated" to "Tâche mise à jour avec succès",
            "message.todo_deleted" to "Tâche supprimée avec succès",
            "message.todos_cleared" to "Tâches terminées effacées",
            
            // Real-time
            "realtime.user_joined" to "a rejoint l'application",
            "realtime.user_left" to "a quitté l'application",
            "realtime.todo_added_by" to "a ajouté une tâche",
            "realtime.todo_updated_by" to "a mis à jour une tâche",
            "realtime.todo_deleted_by" to "a supprimé une tâche",
            "realtime.connected_users" to "Utilisateurs connectés",
            
            // Errors
            "error.server_error" to "Erreur du serveur survenue",
            "error.network_error" to "Erreur réseau. Veuillez réessayer.",
            "error.todo_not_found" to "Tâche non trouvée",
            "error.unauthorized" to "Accès non autorisé"
        )
    )
    
    fun get(key: String, language: Language): String {
        return translations[language]?.get(key) ?: key
    }
    
    fun get(key: String, languageCode: String): String {
        val language = Language.values().find { it.code == languageCode } ?: Language.ENGLISH
        return get(key, language)
    }
}