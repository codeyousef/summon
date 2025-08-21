package code.yousef.summon.examples.js.i18n

import code.yousef.summon.i18n.Language
import code.yousef.summon.i18n.LayoutDirection

/**
 * Translations for the Todo application supporting multiple languages.
 * This demonstrates Summon's i18n capabilities.
 */
object Translations {
    
    // Available languages
    val ENGLISH = Language("en", "English", LayoutDirection.LTR)
    val SPANISH = Language("es", "Español", LayoutDirection.LTR)
    val FRENCH = Language("fr", "Français", LayoutDirection.LTR)
    val ARABIC = Language("ar", "العربية", LayoutDirection.RTL)
    
    val availableLanguages = listOf(ENGLISH, SPANISH, FRENCH, ARABIC)
    
    // Translation strings
    val strings = mapOf(
        "en" to mapOf(
            // App
            "app.title" to "Summon Todo App",
            "app.subtitle" to "A powerful todo app built with pure Summon components",
            
            // Authentication
            "auth.login" to "Login",
            "auth.logout" to "Logout",
            "auth.username" to "Username",
            "auth.password" to "Password",
            "auth.username.placeholder" to "Enter your username",
            "auth.password.placeholder" to "Enter your password",
            "auth.login.button" to "Sign In",
            "auth.welcome.back" to "Welcome back",
            "auth.demo.hint" to "Demo: Use any username with password of 3+ characters",
            
            // Todos
            "todo.add" to "Add Todo",
            "todo.new.placeholder" to "What needs to be done?",
            "todo.filter.all" to "All",
            "todo.filter.active" to "Active",
            "todo.filter.completed" to "Completed",
            "todo.clear.completed" to "Clear Completed",
            "todo.toggle.all" to "Toggle All",
            "todo.priority.high" to "High Priority",
            "todo.priority.normal" to "Normal Priority",
            "todo.priority.low" to "Low Priority",
            "todo.delete" to "Delete",
            "todo.edit" to "Edit",
            "todo.save" to "Save",
            "todo.cancel" to "Cancel",
            
            // Stats
            "stats.total" to "Total",
            "stats.active" to "Active",
            "stats.completed" to "Completed",
            "stats.items.left" to "{count} items left",
            "stats.items.completed" to "{count} items completed",
            
            // Theme
            "theme.light" to "Light Mode",
            "theme.dark" to "Dark Mode",
            "theme.toggle" to "Toggle Theme",
            
            // Language
            "language.select" to "Language",
            "language.current" to "Current Language"
        ),
        
        "es" to mapOf(
            // App
            "app.title" to "Aplicación de Tareas Summon",
            "app.subtitle" to "Una potente aplicación de tareas construida con componentes Summon puros",
            
            // Authentication
            "auth.login" to "Iniciar Sesión",
            "auth.logout" to "Cerrar Sesión",
            "auth.username" to "Usuario",
            "auth.password" to "Contraseña",
            "auth.username.placeholder" to "Ingresa tu usuario",
            "auth.password.placeholder" to "Ingresa tu contraseña",
            "auth.login.button" to "Entrar",
            "auth.welcome.back" to "Bienvenido de vuelta",
            "auth.demo.hint" to "Demo: Usa cualquier usuario con contraseña de 3+ caracteres",
            
            // Todos
            "todo.add" to "Agregar Tarea",
            "todo.new.placeholder" to "¿Qué necesitas hacer?",
            "todo.filter.all" to "Todas",
            "todo.filter.active" to "Activas",
            "todo.filter.completed" to "Completadas",
            "todo.clear.completed" to "Limpiar Completadas",
            "todo.toggle.all" to "Alternar Todas",
            "todo.priority.high" to "Prioridad Alta",
            "todo.priority.normal" to "Prioridad Normal",
            "todo.priority.low" to "Prioridad Baja",
            "todo.delete" to "Eliminar",
            "todo.edit" to "Editar",
            "todo.save" to "Guardar",
            "todo.cancel" to "Cancelar",
            
            // Stats
            "stats.total" to "Total",
            "stats.active" to "Activas",
            "stats.completed" to "Completadas",
            "stats.items.left" to "{count} tareas pendientes",
            "stats.items.completed" to "{count} tareas completadas",
            
            // Theme
            "theme.light" to "Modo Claro",
            "theme.dark" to "Modo Oscuro",
            "theme.toggle" to "Cambiar Tema",
            
            // Language
            "language.select" to "Idioma",
            "language.current" to "Idioma Actual"
        ),
        
        "fr" to mapOf(
            // App
            "app.title" to "Application Todo Summon",
            "app.subtitle" to "Une application todo puissante construite avec des composants Summon purs",
            
            // Authentication
            "auth.login" to "Se Connecter",
            "auth.logout" to "Se Déconnecter",
            "auth.username" to "Nom d'utilisateur",
            "auth.password" to "Mot de passe",
            "auth.username.placeholder" to "Entrez votre nom d'utilisateur",
            "auth.password.placeholder" to "Entrez votre mot de passe",
            "auth.login.button" to "Se Connecter",
            "auth.welcome.back" to "Bienvenue de retour",
            "auth.demo.hint" to "Démo: Utilisez n'importe quel nom d'utilisateur avec un mot de passe de 3+ caractères",
            
            // Todos
            "todo.add" to "Ajouter Tâche",
            "todo.new.placeholder" to "Que faut-il faire?",
            "todo.filter.all" to "Toutes",
            "todo.filter.active" to "Actives",
            "todo.filter.completed" to "Terminées",
            "todo.clear.completed" to "Effacer Terminées",
            "todo.toggle.all" to "Basculer Toutes",
            "todo.priority.high" to "Priorité Élevée",
            "todo.priority.normal" to "Priorité Normale",
            "todo.priority.low" to "Priorité Faible",
            "todo.delete" to "Supprimer",
            "todo.edit" to "Modifier",
            "todo.save" to "Sauvegarder",
            "todo.cancel" to "Annuler",
            
            // Stats
            "stats.total" to "Total",
            "stats.active" to "Actives",
            "stats.completed" to "Terminées",
            "stats.items.left" to "{count} tâches restantes",
            "stats.items.completed" to "{count} tâches terminées",
            
            // Theme
            "theme.light" to "Mode Clair",
            "theme.dark" to "Mode Sombre",
            "theme.toggle" to "Changer le Thème",
            
            // Language
            "language.select" to "Langue",
            "language.current" to "Langue Actuelle"
        ),
        
        "ar" to mapOf(
            // App
            "app.title" to "تطبيق المهام سومون",
            "app.subtitle" to "تطبيق مهام قوي مبني بمكونات سومون النقية",
            
            // Authentication
            "auth.login" to "تسجيل الدخول",
            "auth.logout" to "تسجيل الخروج",
            "auth.username" to "اسم المستخدم",
            "auth.password" to "كلمة المرور",
            "auth.username.placeholder" to "أدخل اسم المستخدم",
            "auth.password.placeholder" to "أدخل كلمة المرور",
            "auth.login.button" to "دخول",
            "auth.welcome.back" to "مرحباً بعودتك",
            "auth.demo.hint" to "تجريبي: استخدم أي اسم مستخدم مع كلمة مرور من 3+ أحرف",
            
            // Todos
            "todo.add" to "إضافة مهمة",
            "todo.new.placeholder" to "ما الذي يجب فعله؟",
            "todo.filter.all" to "الكل",
            "todo.filter.active" to "نشطة",
            "todo.filter.completed" to "مكتملة",
            "todo.clear.completed" to "مسح المكتملة",
            "todo.toggle.all" to "تبديل الكل",
            "todo.priority.high" to "أولوية عالية",
            "todo.priority.normal" to "أولوية عادية",
            "todo.priority.low" to "أولوية منخفضة",
            "todo.delete" to "حذف",
            "todo.edit" to "تعديل",
            "todo.save" to "حفظ",
            "todo.cancel" to "إلغاء",
            
            // Stats
            "stats.total" to "المجموع",
            "stats.active" to "نشطة",
            "stats.completed" to "مكتملة",
            "stats.items.left" to "{count} مهام متبقية",
            "stats.items.completed" to "{count} مهام مكتملة",
            
            // Theme
            "theme.light" to "الوضع الفاتح",
            "theme.dark" to "الوضع الداكن",
            "theme.toggle" to "تبديل المظهر",
            
            // Language
            "language.select" to "اللغة",
            "language.current" to "اللغة الحالية"
        )
    )
    
    /**
     * Get a translated string for the given key and language.
     * Supports simple placeholder replacement with {key} syntax.
     */
    fun getString(key: String, languageCode: String, placeholders: Map<String, String> = emptyMap()): String {
        val languageStrings = strings[languageCode] ?: strings["en"] ?: emptyMap()
        var text = languageStrings[key] ?: key
        
        // Replace placeholders
        placeholders.forEach { (placeholder, value) ->
            text = text.replace("{$placeholder}", value)
        }
        
        return text
    }
    
    /**
     * Get a translated string using a Language object
     */
    fun getString(key: String, language: Language, placeholders: Map<String, String> = emptyMap()): String {
        return getString(key, language.code, placeholders)
    }
}