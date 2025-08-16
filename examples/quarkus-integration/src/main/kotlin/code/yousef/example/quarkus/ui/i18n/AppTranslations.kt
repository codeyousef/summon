package code.yousef.example.quarkus.ui.i18n

import code.yousef.summon.i18n.*

/**
 * Application translations for internationalization
 */
object AppTranslations {
    
    /**
     * Available languages
     */
    val supportedLanguages = listOf(
        Language("en", "English", LayoutDirection.LTR),
        Language("es", "Español", LayoutDirection.LTR),
        Language("fr", "Français", LayoutDirection.LTR),
        Language("ar", "العربية", LayoutDirection.RTL)
    )
    
    /**
     * English translations
     */
    val englishStrings = mapOf(
        // Authentication
        "auth.login" to "Login",
        "auth.logout" to "Logout",
        "auth.register" to "Register",
        "auth.username" to "Username",
        "auth.password" to "Password",
        "auth.email" to "Email",
        "auth.login.success" to "Logged in successfully",
        "auth.logout.success" to "Logged out successfully",
        "auth.register.success" to "Registered successfully",
        "auth.error.invalid" to "Invalid username or password",
        "auth.error.exists" to "Username already exists",
        "auth.error.required" to "Username and password are required",
        
        // Todo Management
        "todo.title" to "Todo Title",
        "todo.description" to "Description",
        "todo.priority" to "Priority",
        "todo.dueDate" to "Due Date",
        "todo.completed" to "Completed",
        "todo.pending" to "Pending",
        "todo.add" to "Add Todo",
        "todo.edit" to "Edit Todo",
        "todo.delete" to "Delete Todo",
        "todo.save" to "Save",
        "todo.cancel" to "Cancel",
        "todo.mark.complete" to "Mark as Complete",
        "todo.mark.incomplete" to "Mark as Incomplete",
        "todo.delete.confirm" to "Are you sure you want to delete this todo?",
        
        // Priority levels
        "priority.low" to "Low",
        "priority.medium" to "Medium",
        "priority.high" to "High",
        "priority.urgent" to "Urgent",
        
        // Statistics
        "stats.total" to "Total",
        "stats.completed" to "Completed",
        "stats.pending" to "Pending",
        "stats.overdue" to "Overdue",
        "stats.high.priority" to "High Priority",
        
        // Navigation
        "nav.dashboard" to "Dashboard",
        "nav.todos" to "Todos",
        "nav.settings" to "Settings",
        
        // Settings
        "settings.theme" to "Theme",
        "settings.language" to "Language",
        "settings.theme.light" to "Light",
        "settings.theme.dark" to "Dark",
        "settings.theme.auto" to "Auto",
        
        // Common
        "common.loading" to "Loading...",
        "common.error" to "Error",
        "common.success" to "Success",
        "common.yes" to "Yes",
        "common.no" to "No",
        "common.close" to "Close",
        "common.search" to "Search",
        "common.filter" to "Filter",
        "common.sort" to "Sort",
        
        // Welcome messages
        "welcome.title" to "Welcome to Todo App",
        "welcome.subtitle" to "Manage your tasks efficiently with Summon UI",
        "welcome.demo.note" to "You're logged in as demo user. Try creating, editing, and completing todos!"
    )
    
    /**
     * Spanish translations
     */
    val spanishStrings = mapOf(
        // Authentication
        "auth.login" to "Iniciar Sesión",
        "auth.logout" to "Cerrar Sesión",
        "auth.register" to "Registrar",
        "auth.username" to "Usuario",
        "auth.password" to "Contraseña",
        "auth.email" to "Correo Electrónico",
        "auth.login.success" to "Sesión iniciada exitosamente",
        "auth.logout.success" to "Sesión cerrada exitosamente",
        "auth.register.success" to "Registro exitoso",
        "auth.error.invalid" to "Usuario o contraseña inválidos",
        "auth.error.exists" to "El usuario ya existe",
        "auth.error.required" to "Usuario y contraseña son requeridos",
        
        // Todo Management
        "todo.title" to "Título de la Tarea",
        "todo.description" to "Descripción",
        "todo.priority" to "Prioridad",
        "todo.dueDate" to "Fecha Límite",
        "todo.completed" to "Completada",
        "todo.pending" to "Pendiente",
        "todo.add" to "Agregar Tarea",
        "todo.edit" to "Editar Tarea",
        "todo.delete" to "Eliminar Tarea",
        "todo.save" to "Guardar",
        "todo.cancel" to "Cancelar",
        "todo.mark.complete" to "Marcar como Completada",
        "todo.mark.incomplete" to "Marcar como Pendiente",
        "todo.delete.confirm" to "¿Estás seguro de que quieres eliminar esta tarea?",
        
        // Priority levels
        "priority.low" to "Baja",
        "priority.medium" to "Media",
        "priority.high" to "Alta",
        "priority.urgent" to "Urgente",
        
        // Statistics
        "stats.total" to "Total",
        "stats.completed" to "Completadas",
        "stats.pending" to "Pendientes",
        "stats.overdue" to "Vencidas",
        "stats.high.priority" to "Alta Prioridad",
        
        // Navigation
        "nav.dashboard" to "Panel",
        "nav.todos" to "Tareas",
        "nav.settings" to "Configuración",
        
        // Settings
        "settings.theme" to "Tema",
        "settings.language" to "Idioma",
        "settings.theme.light" to "Claro",
        "settings.theme.dark" to "Oscuro",
        "settings.theme.auto" to "Automático",
        
        // Common
        "common.loading" to "Cargando...",
        "common.error" to "Error",
        "common.success" to "Éxito",
        "common.yes" to "Sí",
        "common.no" to "No",
        "common.close" to "Cerrar",
        "common.search" to "Buscar",
        "common.filter" to "Filtrar",
        "common.sort" to "Ordenar",
        
        // Welcome messages
        "welcome.title" to "Bienvenido a la Aplicación de Tareas",
        "welcome.subtitle" to "Gestiona tus tareas eficientemente con Summon UI",
        "welcome.demo.note" to "Has iniciado sesión como usuario demo. ¡Prueba crear, editar y completar tareas!"
    )
    
    /**
     * French translations
     */
    val frenchStrings = mapOf(
        // Authentication
        "auth.login" to "Connexion",
        "auth.logout" to "Déconnexion",
        "auth.register" to "S'inscrire",
        "auth.username" to "Nom d'utilisateur",
        "auth.password" to "Mot de passe",
        "auth.email" to "E-mail",
        "auth.login.success" to "Connexion réussie",
        "auth.logout.success" to "Déconnexion réussie",
        "auth.register.success" to "Inscription réussie",
        "auth.error.invalid" to "Nom d'utilisateur ou mot de passe invalide",
        "auth.error.exists" to "Le nom d'utilisateur existe déjà",
        "auth.error.required" to "Nom d'utilisateur et mot de passe requis",
        
        // Todo Management
        "todo.title" to "Titre de la Tâche",
        "todo.description" to "Description",
        "todo.priority" to "Priorité",
        "todo.dueDate" to "Date d'échéance",
        "todo.completed" to "Terminée",
        "todo.pending" to "En attente",
        "todo.add" to "Ajouter une Tâche",
        "todo.edit" to "Modifier la Tâche",
        "todo.delete" to "Supprimer la Tâche",
        "todo.save" to "Enregistrer",
        "todo.cancel" to "Annuler",
        "todo.mark.complete" to "Marquer comme terminée",
        "todo.mark.incomplete" to "Marquer comme en attente",
        "todo.delete.confirm" to "Êtes-vous sûr de vouloir supprimer cette tâche?",
        
        // Priority levels
        "priority.low" to "Faible",
        "priority.medium" to "Moyenne",
        "priority.high" to "Élevée",
        "priority.urgent" to "Urgente",
        
        // Statistics
        "stats.total" to "Total",
        "stats.completed" to "Terminées",
        "stats.pending" to "En attente",
        "stats.overdue" to "En retard",
        "stats.high.priority" to "Haute Priorité",
        
        // Navigation
        "nav.dashboard" to "Tableau de bord",
        "nav.todos" to "Tâches",
        "nav.settings" to "Paramètres",
        
        // Settings
        "settings.theme" to "Thème",
        "settings.language" to "Langue",
        "settings.theme.light" to "Clair",
        "settings.theme.dark" to "Sombre",
        "settings.theme.auto" to "Automatique",
        
        // Common
        "common.loading" to "Chargement...",
        "common.error" to "Erreur",
        "common.success" to "Succès",
        "common.yes" to "Oui",
        "common.no" to "Non",
        "common.close" to "Fermer",
        "common.search" to "Rechercher",
        "common.filter" to "Filtrer",
        "common.sort" to "Trier",
        
        // Welcome messages
        "welcome.title" to "Bienvenue dans l'Application de Tâches",
        "welcome.subtitle" to "Gérez vos tâches efficacement avec Summon UI",
        "welcome.demo.note" to "Vous êtes connecté en tant qu'utilisateur démo. Essayez de créer, modifier et terminer des tâches!"
    )
    
    /**
     * Arabic translations (RTL)
     */
    val arabicStrings = mapOf(
        // Authentication
        "auth.login" to "تسجيل الدخول",
        "auth.logout" to "تسجيل الخروج",
        "auth.register" to "إنشاء حساب",
        "auth.username" to "اسم المستخدم",
        "auth.password" to "كلمة المرور",
        "auth.email" to "البريد الإلكتروني",
        "auth.login.success" to "تم تسجيل الدخول بنجاح",
        "auth.logout.success" to "تم تسجيل الخروج بنجاح",
        "auth.register.success" to "تم إنشاء الحساب بنجاح",
        "auth.error.invalid" to "اسم المستخدم أو كلمة المرور غير صحيحة",
        "auth.error.exists" to "اسم المستخدم موجود بالفعل",
        "auth.error.required" to "اسم المستخدم وكلمة المرور مطلوبان",
        
        // Todo Management
        "todo.title" to "عنوان المهمة",
        "todo.description" to "الوصف",
        "todo.priority" to "الأولوية",
        "todo.dueDate" to "تاريخ الاستحقاق",
        "todo.completed" to "مكتملة",
        "todo.pending" to "قيد الانتظار",
        "todo.add" to "إضافة مهمة",
        "todo.edit" to "تعديل المهمة",
        "todo.delete" to "حذف المهمة",
        "todo.save" to "حفظ",
        "todo.cancel" to "إلغاء",
        "todo.mark.complete" to "تحديد كمكتملة",
        "todo.mark.incomplete" to "تحديد كغير مكتملة",
        "todo.delete.confirm" to "هل أنت متأكد من حذف هذه المهمة؟",
        
        // Priority levels
        "priority.low" to "منخفضة",
        "priority.medium" to "متوسطة",
        "priority.high" to "عالية",
        "priority.urgent" to "عاجلة",
        
        // Statistics
        "stats.total" to "المجموع",
        "stats.completed" to "مكتملة",
        "stats.pending" to "قيد الانتظار",
        "stats.overdue" to "متأخرة",
        "stats.high.priority" to "أولوية عالية",
        
        // Navigation
        "nav.dashboard" to "لوحة التحكم",
        "nav.todos" to "المهام",
        "nav.settings" to "الإعدادات",
        
        // Settings
        "settings.theme" to "المظهر",
        "settings.language" to "اللغة",
        "settings.theme.light" to "فاتح",
        "settings.theme.dark" to "داكن",
        "settings.theme.auto" to "تلقائي",
        
        // Common
        "common.loading" to "جاري التحميل...",
        "common.error" to "خطأ",
        "common.success" to "نجح",
        "common.yes" to "نعم",
        "common.no" to "لا",
        "common.close" to "إغلاق",
        "common.search" to "بحث",
        "common.filter" to "تصفية",
        "common.sort" to "ترتيب",
        
        // Welcome messages
        "welcome.title" to "مرحباً بك في تطبيق المهام",
        "welcome.subtitle" to "أدر مهامك بكفاءة مع Summon UI",
        "welcome.demo.note" to "لقد سجلت الدخول كمستخدم تجريبي. جرب إنشاء وتعديل وإكمال المهام!"
    )
    
    /**
     * Gets translation for a key in the specified language
     */
    fun getString(key: String, languageCode: String): String {
        val translations = when (languageCode) {
            "es" -> spanishStrings
            "fr" -> frenchStrings
            "ar" -> arabicStrings
            else -> englishStrings
        }
        return translations[key] ?: englishStrings[key] ?: key
    }
}